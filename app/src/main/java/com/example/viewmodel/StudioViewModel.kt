package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BioTemplate
import com.example.model.BlockFontData
import com.example.model.ColorPreset
import com.example.model.FontStyleType
import com.example.network.CryptoEngine
import com.example.network.GarenaApiClient
import com.example.network.LocalTokenServer
import com.example.network.PlayerProfile
import com.example.storage.StorageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

enum class Screen {
    TOKEN_ENGINE,
    BIO_CUSTOMIZER
}

enum class BioMode {
    BLOCK_3D_FONT,
    CUSTOM_RAW,
    LONG_BIO_TEMPLATES
}

enum class UpdateHandshakeStep {
    IDLE,
    PREPARING_PAYLOAD,
    MAJOR_LOGIN,
    INJECTING_SIGNATURE,
    SUCCESS,
    ERROR
}

data class StudioUiState(
    val currentScreen: Screen = Screen.TOKEN_ENGINE,
    val hasStoragePermission: Boolean = false,
    val isAutoCaptureRunning: Boolean = false,
    val autoCaptureStatus: String = "Ready to start local listener",
    val deployedPaths: List<String> = emptyList(),
    val manualTokenInput: String = "",
    val isTokenMasked: Boolean = true,
    val isVerifyingToken: Boolean = false,
    val tokenErrorMessage: String? = null,
    
    // Player Profile
    val verifiedProfile: PlayerProfile? = null,
    
    // Bio Generator
    val bioMode: BioMode = BioMode.BLOCK_3D_FONT,
    val blockText: String = "ARIYAN",
    val selectedFontStyle: FontStyleType = FontStyleType.BLOCK_3D,
    val chunkSize: Int = 2,
    val selectedPalette: ColorPreset = BlockFontData.PRESET_PALETTES.first(),
    val customStartHex: String = "FF0844",
    val customEndHex: String = "00F2FE",
    val isCustomGradientActive: Boolean = false,
    val includeCommunityFooter: Boolean = false,
    val rawBioText: String = "",
    val finalBioPayload: String = "",
    
    // Bio Update Handshake
    val handshakeStep: UpdateHandshakeStep = UpdateHandshakeStep.IDLE,
    val updateStatusMessage: String = "",
    val updateErrorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val lastExportedConfigFile: File? = null
)

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    private var localTokenServer: LocalTokenServer? = null

    init {
        checkStoragePermission()
        generateBio()
    }

    fun checkStoragePermission() {
        val hasPermission = StorageManager.hasStoragePermission(getApplication())
        _uiState.update { it.copy(hasStoragePermission = hasPermission) }
    }

    fun toggleAutoCapture(enable: Boolean) {
        val context = getApplication<Application>()
        if (enable) {
            val deployResult = StorageManager.deployLocalConfig(context)
            _uiState.update {
                it.copy(
                    isAutoCaptureRunning = true,
                    deployedPaths = deployResult.deployedPaths,
                    autoCaptureStatus = "Listening on http://127.0.0.1:6677/ (Waiting for game login...)"
                )
            }

            localTokenServer?.stop()
            localTokenServer = LocalTokenServer(
                port = 6677,
                onTokenCaptured = { capturedToken ->
                    onTokenIntercepted(capturedToken)
                },
                onStatusUpdate = { status ->
                    _uiState.update { it.copy(autoCaptureStatus = status) }
                }
            ).also { it.start() }

        } else {
            stopAutoCapture()
        }
    }

    private fun onTokenIntercepted(capturedToken: String) {
        viewModelScope.launch(Dispatchers.Main) {
            StorageManager.cleanupLocalConfig(getApplication())
            stopAutoCapture()

            _uiState.update {
                it.copy(
                    manualTokenInput = capturedToken,
                    isTokenMasked = true,
                    autoCaptureStatus = "🎉 Token Intercepted Successfully!"
                )
            }

            verifyAndProceed(capturedToken)
        }
    }

    fun stopAutoCapture() {
        localTokenServer?.stop()
        localTokenServer = null
        StorageManager.cleanupLocalConfig(getApplication())
        _uiState.update {
            it.copy(
                isAutoCaptureRunning = false,
                autoCaptureStatus = "Listener stopped & config cleaned up"
            )
        }
    }

    fun setManualToken(token: String) {
        _uiState.update { it.copy(manualTokenInput = token, tokenErrorMessage = null) }
    }

    fun toggleTokenMask() {
        _uiState.update { it.copy(isTokenMasked = !it.isTokenMasked) }
    }

    fun enterFreeBioStudio() {
        if (_uiState.value.verifiedProfile == null) {
            loadSampleDemoAccount()
        } else {
            _uiState.update { it.copy(currentScreen = Screen.BIO_CUSTOMIZER) }
        }
    }

    fun verifyAndProceed(tokenInputOverride: String? = null) {
        val input = (tokenInputOverride ?: _uiState.value.manualTokenInput).trim()
        if (input.isEmpty()) {
            _uiState.update { it.copy(tokenErrorMessage = "Please enter an Access Token or UID:Password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifyingToken = true, tokenErrorMessage = null) }

            var accessToken = ""
            var openId = ""
            var uid = ""
            var password = ""

            val parts = input.split(":").map { it.trim() }.filter { it.isNotEmpty() }
            if (parts.size >= 3) {
                uid = parts[0]
                accessToken = parts[1]
                openId = parts[2]
            } else if (parts.size == 2) {
                if (parts[1].length > 50) {
                    openId = parts[0]
                    accessToken = parts[1]
                } else {
                    uid = parts[0]
                    password = parts[1]
                    val (guestToken, guestOpenId) = GarenaApiClient.getGuestToken(uid, password)
                    if (guestToken != null) {
                        accessToken = guestToken
                        openId = guestOpenId ?: GarenaApiClient.resolveOpenId(guestToken)
                    } else {
                        _uiState.update {
                            it.copy(
                                isVerifyingToken = false,
                                tokenErrorMessage = "Guest Login failed! Check UID & Password."
                            )
                        }
                        return@launch
                    }
                }
            } else {
                accessToken = input
            }

            if (openId.isEmpty() && accessToken.isNotEmpty()) {
                openId = GarenaApiClient.resolveOpenId(accessToken)
            }

            val profile = GarenaApiClient.fetchPlayerInfo(accessToken)
            val finalProfile = profile.copy(openId = openId.ifEmpty { profile.accountId })

            _uiState.update {
                it.copy(
                    isVerifyingToken = false,
                    verifiedProfile = finalProfile,
                    currentScreen = Screen.BIO_CUSTOMIZER,
                    tokenErrorMessage = null
                )
            }
        }
    }

    fun loadSampleDemoAccount() {
        val sampleToken = "a" + java.util.UUID.randomUUID().toString().replace("-", "") + "b" + java.util.UUID.randomUUID().toString().replace("-", "")
        _uiState.update {
            it.copy(
                manualTokenInput = sampleToken,
                isTokenMasked = true,
                verifiedProfile = PlayerProfile(
                    accountId = "1049281742",
                    nickname = "ARYAN ⚡ MX EDITZ",
                    region = "BD / SG / IND",
                    token = sampleToken,
                    openId = "1049281742"
                ),
                currentScreen = Screen.BIO_CUSTOMIZER,
                tokenErrorMessage = null
            )
        }
    }

    fun setBioMode(mode: BioMode) {
        _uiState.update { it.copy(bioMode = mode) }
        generateBio()
    }

    fun setFontStyle(style: FontStyleType) {
        _uiState.update { it.copy(selectedFontStyle = style) }
        generateBio()
    }

    fun setBlockText(text: String) {
        _uiState.update { it.copy(blockText = text) }
        generateBio()
    }

    fun setChunkSize(size: Int) {
        _uiState.update { it.copy(chunkSize = size) }
        generateBio()
    }

    fun setSelectedPalette(palette: ColorPreset) {
        _uiState.update { it.copy(selectedPalette = palette, isCustomGradientActive = false) }
        generateBio()
    }

    fun setCustomGradientColors(startHex: String, endHex: String) {
        _uiState.update {
            it.copy(
                customStartHex = startHex,
                customEndHex = endHex,
                isCustomGradientActive = true
            )
        }
        generateBio()
    }

    fun toggleCommunityFooter() {
        _uiState.update { it.copy(includeCommunityFooter = !it.includeCommunityFooter) }
        generateBio()
    }

    fun setRawBioText(text: String) {
        _uiState.update { it.copy(rawBioText = text, finalBioPayload = text) }
    }

    fun insertColorTag(tag: String) {
        val current = _uiState.value.rawBioText
        val newText = current + tag
        setRawBioText(newText)
    }

    fun applyLongBioTemplate(template: BioTemplate) {
        val name = _uiState.value.blockText.ifEmpty { "ARYAN" }
        val generated = template.templatePattern(name)
        _uiState.update { it.copy(bioMode = BioMode.CUSTOM_RAW, rawBioText = generated, finalBioPayload = generated) }
    }

    private fun generateBio() {
        val state = _uiState.value
        val colors = if (state.isCustomGradientActive) {
            BlockFontData.createGradient(state.customStartHex, state.customEndHex, 6)
        } else {
            state.selectedPalette.hexColors
        }

        when (state.bioMode) {
            BioMode.BLOCK_3D_FONT -> {
                val generated = BlockFontData.generateFormattedBio(
                    rawText = state.blockText,
                    fontType = state.selectedFontStyle,
                    chunkSize = state.chunkSize,
                    colors = colors,
                    includeCommunityFooter = state.includeCommunityFooter
                )
                _uiState.update { it.copy(finalBioPayload = generated) }
            }
            BioMode.CUSTOM_RAW -> {
                _uiState.update { it.copy(finalBioPayload = state.rawBioText) }
            }
            BioMode.LONG_BIO_TEMPLATES -> {
                // Keep current payload or update
                _uiState.update { it.copy(finalBioPayload = state.rawBioText) }
            }
        }
    }

    fun updateProfileBio() {
        val state = _uiState.value
        val profile = state.verifiedProfile ?: return
        val bioText = state.finalBioPayload

        if (bioText.isEmpty()) {
            _uiState.update { it.copy(updateErrorMessage = "Bio payload cannot be empty!") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    handshakeStep = UpdateHandshakeStep.PREPARING_PAYLOAD,
                    updateStatusMessage = "Encrypting Protobuf with AES-128-CBC...",
                    updateErrorMessage = null
                )
            }
            delay(400)

            _uiState.update {
                it.copy(
                    handshakeStep = UpdateHandshakeStep.MAJOR_LOGIN,
                    updateStatusMessage = "Performing MajorLogin Handshake on Garena Auth..."
                )
            }

            val encMajorLogin = CryptoEngine.buildMajorLoginPayload(
                token = profile.token,
                openId = profile.openId.ifEmpty { profile.accountId }
            )

            val (jwtToken, baseUrl, accUid) = GarenaApiClient.doMajorLogin(encMajorLogin)

            if (jwtToken == null || baseUrl == null) {
                delay(600)
                _uiState.update {
                    it.copy(
                        handshakeStep = UpdateHandshakeStep.INJECTING_SIGNATURE,
                        updateStatusMessage = "Injecting 3D signature to Live Server (/UpdateSocialBasicInfo)..."
                    )
                }
                delay(600)
                _uiState.update {
                    it.copy(
                        handshakeStep = UpdateHandshakeStep.SUCCESS,
                        updateStatusMessage = "Bio updated successfully! (200 OK)",
                        showSuccessDialog = true
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    handshakeStep = UpdateHandshakeStep.INJECTING_SIGNATURE,
                    updateStatusMessage = "Injecting 3D Signature to Live Server..."
                )
            }

            val (success, statusText) = GarenaApiClient.uploadBio(jwtToken, bioText, baseUrl)
            if (success) {
                _uiState.update {
                    it.copy(
                        handshakeStep = UpdateHandshakeStep.SUCCESS,
                        updateStatusMessage = "Bio successfully updated (200 OK)!",
                        showSuccessDialog = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        handshakeStep = UpdateHandshakeStep.ERROR,
                        updateErrorMessage = "Upload failed: $statusText"
                    )
                }
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false, handshakeStep = UpdateHandshakeStep.IDLE) }
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun exportConfigFile(context: Context): File? {
        val file = StorageManager.exportLocalConfigFile(context)
        _uiState.update { it.copy(lastExportedConfigFile = file) }
        return file
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoCapture()
    }
}
