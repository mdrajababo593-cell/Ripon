package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BioTemplate
import com.example.model.BlockFontData
import com.example.model.ColorPreset
import com.example.model.FontStyleType
import com.example.network.PlayerProfile
import com.example.ui.components.CommunityButtonsRow
import com.example.ui.components.CommunityCardEndBioChange
import com.example.ui.components.CommunityLinks
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CyberGradientCyan
import com.example.ui.theme.CyberGradientPink
import com.example.ui.theme.CyberGradientPrimary
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.SurfaceGlass
import com.example.ui.theme.SurfaceNight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BioMode
import com.example.viewmodel.Screen
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel
import com.example.viewmodel.UpdateHandshakeStep

@Composable
fun BioCustomizerScreen(
    state: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val profile = state.verifiedProfile

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.TOKEN_ENGINE) },
                modifier = Modifier.testTag("back_to_token_engine_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonCyan
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.dp, NeonCyan, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aryan_profile_avatar),
                        contentDescription = "Aryan",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ARYAN LONG BIO STUDIO",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = TextPrimary
                )
            }

            IconButton(
                onClick = {
                    viewModel.stopAutoCapture()
                    viewModel.navigateTo(Screen.TOKEN_ENGINE)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SwitchAccount,
                    contentDescription = "Switch Account",
                    tint = NeonPink
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Verified Player Banner
        if (profile != null) {
            VerifiedPlayerBanner(
                profile = profile,
                onCopyUid = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("UID", profile.accountId)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "UID Copied!", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Mode Selector Tabs (3D Font Generator vs Custom Raw Editor vs Long Bio Templates)
        TabRow(
            selectedTabIndex = when (state.bioMode) {
                BioMode.BLOCK_3D_FONT -> 0
                BioMode.CUSTOM_RAW -> 1
                BioMode.LONG_BIO_TEMPLATES -> 2
            },
            containerColor = SurfaceGlass,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                val tabIdx = when (state.bioMode) {
                    BioMode.BLOCK_3D_FONT -> 0
                    BioMode.CUSTOM_RAW -> 1
                    BioMode.LONG_BIO_TEMPLATES -> 2
                }
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[tabIdx]),
                    color = NeonCyan,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderGlass, RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = state.bioMode == BioMode.BLOCK_3D_FONT,
                onClick = { viewModel.setBioMode(BioMode.BLOCK_3D_FONT) },
                text = {
                    Text(
                        text = "3D Fonts",
                        fontSize = 12.sp,
                        fontWeight = if (state.bioMode == BioMode.BLOCK_3D_FONT) FontWeight.Bold else FontWeight.Normal,
                        color = if (state.bioMode == BioMode.BLOCK_3D_FONT) NeonCyan else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_3d_font_generator")
            )
            Tab(
                selected = state.bioMode == BioMode.CUSTOM_RAW,
                onClick = { viewModel.setBioMode(BioMode.CUSTOM_RAW) },
                text = {
                    Text(
                        text = "Custom Raw",
                        fontSize = 12.sp,
                        fontWeight = if (state.bioMode == BioMode.CUSTOM_RAW) FontWeight.Bold else FontWeight.Normal,
                        color = if (state.bioMode == BioMode.CUSTOM_RAW) NeonPink else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_custom_raw_editor")
            )
            Tab(
                selected = state.bioMode == BioMode.LONG_BIO_TEMPLATES,
                onClick = { viewModel.setBioMode(BioMode.LONG_BIO_TEMPLATES) },
                text = {
                    Text(
                        text = "VIP Templates",
                        fontSize = 12.sp,
                        fontWeight = if (state.bioMode == BioMode.LONG_BIO_TEMPLATES) FontWeight.Bold else FontWeight.Normal,
                        color = if (state.bioMode == BioMode.LONG_BIO_TEMPLATES) NeonYellow else TextSecondary
                    )
                },
                modifier = Modifier.testTag("tab_long_bio_templates")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode Content
        when (state.bioMode) {
            BioMode.BLOCK_3D_FONT -> {
                Block3DFontControls(
                    text = state.blockText,
                    selectedStyle = state.selectedFontStyle,
                    chunkSize = state.chunkSize,
                    selectedPalette = state.selectedPalette,
                    customStartHex = state.customStartHex,
                    customEndHex = state.customEndHex,
                    isCustomGradientActive = state.isCustomGradientActive,
                    includeCommunityFooter = state.includeCommunityFooter,
                    onTextChange = { viewModel.setBlockText(it) },
                    onStyleSelect = { viewModel.setFontStyle(it) },
                    onChunkChange = { viewModel.setChunkSize(it) },
                    onPaletteSelect = { viewModel.setSelectedPalette(it) },
                    onCustomGradientChange = { start, end -> viewModel.setCustomGradientColors(start, end) },
                    onToggleCommunityFooter = { viewModel.toggleCommunityFooter() }
                )
            }
            BioMode.CUSTOM_RAW -> {
                CustomRawControls(
                    rawText = state.rawBioText,
                    onTextChange = { viewModel.setRawBioText(it) },
                    onInsertTag = { viewModel.insertColorTag(it) }
                )
            }
            BioMode.LONG_BIO_TEMPLATES -> {
                LongBioTemplatesControls(
                    onSelectTemplate = { template ->
                        viewModel.applyLongBioTemplate(template)
                        Toast.makeText(context, "Template applied to Bio!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Real-Time In-Game Free Fire Profile Card & Avatar Live Preview
        com.example.ui.components.FullProfileCardPreview(
            profile = profile,
            bioPayload = state.finalBioPayload,
            onCopyBio = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("3D Block Bio", state.finalBioPayload)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "3D Bio copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Update Profile Bio Button & Progress Handshake
        UpdateProfileSection(
            handshakeStep = state.handshakeStep,
            statusMessage = state.updateStatusMessage,
            errorMessage = state.updateErrorMessage,
            onUpdateClick = { viewModel.updateProfileBio() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Community Channel Bar
        CommunityCardEndBioChange()

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Success Dialog on Bio Update (Shows Official WhatsApp & Telegram links)
    if (state.showSuccessDialog) {
        SuccessBioDialog(
            profile = profile ?: PlayerProfile(accountId = "1049281742", nickname = "ARYAN ⚡ VIP", region = "BD/IND", token = "", openId = ""),
            bioPayload = state.finalBioPayload,
            onDismiss = { viewModel.dismissSuccessDialog() }
        )
    }
}

@Composable
private fun VerifiedPlayerBanner(
    profile: PlayerProfile,
    onCopyUid: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BorderPink, RoundedCornerShape(16.dp)),
        color = SurfaceCard
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CyberGradientPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.nickname,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VERIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "UID: ${profile.accountId}",
                            fontSize = 11.sp,
                            color = NeonYellow,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy UID",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(12.dp)
                                .clickable { onCopyUid() }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Region",
                            tint = NeonCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = profile.region,
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Block3DFontControls(
    text: String,
    selectedStyle: FontStyleType,
    chunkSize: Int,
    selectedPalette: ColorPreset,
    customStartHex: String,
    customEndHex: String,
    isCustomGradientActive: Boolean,
    includeCommunityFooter: Boolean,
    onTextChange: (String) -> Unit,
    onStyleSelect: (FontStyleType) -> Unit,
    onChunkChange: (Int) -> Unit,
    onPaletteSelect: (ColorPreset) -> Unit,
    onCustomGradientChange: (String, String) -> Unit,
    onToggleCommunityFooter: () -> Unit
) {
    var customStart by remember { mutableStateOf(customStartHex) }
    var customEnd by remember { mutableStateOf(customEndHex) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderGlass, RoundedCornerShape(20.dp)),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Text Input Field
            Text(
                text = "SIGNATURE TEXT / NICKNAME",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("block_text_input"),
                placeholder = { Text("e.g. ARYAN, MX, 777", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderGlass,
                    focusedContainerColor = SurfaceNight,
                    unfocusedContainerColor = SurfaceNight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Font Style Selector
            Text(
                text = "SELECT FONT STYLE",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FontStyleType.values().forEach { style ->
                    val isSelected = selectedStyle == style
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else BorderGlass,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onStyleSelect(style) },
                        color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else SurfaceNight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = style.iconDesc,
                                fontSize = 11.sp,
                                color = if (isSelected) NeonCyan else NeonPink,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = style.label,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Palettes
            Text(
                text = "NEON COLOR GRADIENT PRESETS",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BlockFontData.PRESET_PALETTES.forEach { preset ->
                    val isSelected = !isCustomGradientActive && selectedPalette == preset
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.5.dp,
                                if (isSelected) NeonPink else BorderGlass,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onPaletteSelect(preset) },
                        color = if (isSelected) NeonPink.copy(alpha = 0.15f) else SurfaceNight
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Color dots
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                preset.hexColors.take(4).forEach { hex ->
                                    val r = hex.substring(0, 2).toIntOrNull(16) ?: 255
                                    val g = hex.substring(2, 4).toIntOrNull(16) ?: 255
                                    val b = hex.substring(4, 6).toIntOrNull(16) ?: 255
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(r, g, b))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = preset.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Color Hex Gradient
            Text(
                text = "CUSTOM 2-COLOR GRADIENT (HEX)",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customStart,
                    onValueChange = {
                        customStart = it.uppercase().take(6)
                        if (customStart.length == 6 && customEnd.length == 6) {
                            onCustomGradientChange(customStart, customEnd)
                        }
                    },
                    label = { Text("Start Hex", fontSize = 10.sp) },
                    placeholder = { Text("FF0844") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = BorderGlass,
                        focusedContainerColor = SurfaceNight,
                        unfocusedContainerColor = SurfaceNight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = customEnd,
                    onValueChange = {
                        customEnd = it.uppercase().take(6)
                        if (customStart.length == 6 && customEnd.length == 6) {
                            onCustomGradientChange(customStart, customEnd)
                        }
                    },
                    label = { Text("End Hex", fontSize = 10.sp) },
                    placeholder = { Text("00F2FE") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderGlass,
                        focusedContainerColor = SurfaceNight,
                        unfocusedContainerColor = SurfaceNight,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Button(
                    onClick = {
                        if (customStart.length == 6 && customEnd.length == 6) {
                            onCustomGradientChange(customStart, customEnd)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chunk Size (Colors per characters)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "COLOR CHUNK STEP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 2, 3).forEach { size ->
                        val isSelected = chunkSize == size
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) NeonCyan else BorderGlass,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onChunkChange(size) },
                            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else SurfaceNight
                        ) {
                            Text(
                                text = "$size Char",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) NeonCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Community Footer Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceNight)
                    .clickable { onToggleCommunityFooter() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Append ARYAN VIP Channel Tag",
                    fontSize = 11.sp,
                    color = if (includeCommunityFooter) NeonCyan else TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (includeCommunityFooter) NeonCyan else BorderGlass),
                    contentAlignment = Alignment.Center
                ) {
                    if (includeCommunityFooter) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checked",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomRawControls(
    rawText: String,
    onTextChange: (String) -> Unit,
    onInsertTag: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderGlass, RoundedCornerShape(20.dp)),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "CUSTOM RAW BIO EDITOR",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Combine tags like [b], [c], [i], [u] with hex colors e.g. [FF0844] or emojis",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Tag Palette
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "[b]" to "Bold",
                    "[c]" to "Center",
                    "[i]" to "Italic",
                    "[u]" to "Underline",
                    "[s]" to "Strike",
                    "[FF0844]" to "Pink",
                    "[00F2FE]" to "Cyan",
                    "[00FF66]" to "Green",
                    "[FFFF00]" to "Yellow",
                    "[7F00FF]" to "Purple",
                    "[FF4500]" to "Orange",
                    "[FFFFFF]" to "White",
                    "⚡" to "Bolt",
                    "亗" to "Crown",
                    "★" to "Star"
                ).forEach { (tag, label) ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, BorderGlass, RoundedCornerShape(8.dp))
                            .clickable { onInsertTag(tag) },
                        color = SurfaceNight
                    ) {
                        Text(
                            text = "$tag $label",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rawText,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("raw_bio_text_field"),
                placeholder = {
                    Text(
                        "[b][c][FF0844]⚡ ARYAN VIP BIO ⚡\n[b][c][00F2FE]亗 BD PRO SQUAD 亗\n[b][c][00FF66]t.me/Premiume_FF_Tcp_bot_Community",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = BorderGlass,
                    focusedContainerColor = SurfaceNight,
                    unfocusedContainerColor = SurfaceNight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun LongBioTemplatesControls(
    onSelectTemplate: (BioTemplate) -> Unit
) {
    val templates = listOf(
        BioTemplate(
            name = "Aryan Royal Wings VIP",
            description = "Dual wing brackets with bold cyan & pink gradient tags",
            templatePattern = { name ->
                "[b][c][FF0844]꧁༺ [00F2FE]⚡ $name ⚡ [FF0844]༻꧂\n[b][c][00FF66]━━━━━━ 亗 VIP BIO 亗 ━━━━━━\n[b][c][FFD700]★ GRANDMASTER BD PRO ★\n[b][c][00F2FE]t.me/Premiume_FF_Tcp_bot_Community"
            }
        ),
        BioTemplate(
            name = "Cyberpunk Neo Banner",
            description = "3-Line stylized esports signature with matrix accents",
            templatePattern = { name ->
                "[b][c][00F2FE]╔═══════════════════╗\n[b][c][FF0844]  ⚡ $name ⚡  \n[b][c][00FF66]  MX EDITZ VIP TCP HUB  \n[b][c][00F2FE]╚═══════════════════╝"
            }
        ),
        BioTemplate(
            name = "Flame & Thunder Signature",
            description = "High-contrast gold flame and cyber thunder",
            templatePattern = { name ->
                "[b][c][FFD700]⚡ 亗 $name 亗 ⚡\n[b][c][FF4500]★ ONE TAP HEADSHOT KING ★\n[b][c][00F2FE]WHATSAPP: channel/0029Vb7jk7n6mYPIZIHDeV1T"
            }
        ),
        BioTemplate(
            name = "Japanese Katakana Clan",
            description = "Katakana brackets with neon violet aesthetic",
            templatePattern = { name ->
                "[b][c][7F00FF]【 $name 】\n[b][c][00F2FE]✦ FREE FIRE ELITE GUILD ✦\n[b][c][FF0844]t.me/Premiume_FF_Tcp_bot_Community"
            }
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderGlass, RoundedCornerShape(20.dp)),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "VIP LONG BIO & SIGNATURE TEMPLATES",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            templates.forEach { tmpl ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderGlass, RoundedCornerShape(12.dp))
                        .clickable { onSelectTemplate(tmpl) },
                    color = SurfaceNight
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tmpl.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Text(
                                text = tmpl.description,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Button(
                            onClick = { onSelectTemplate(tmpl) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Use", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InGameSimulatorCard(
    profile: PlayerProfile?,
    bioPayload: String,
    onCopyBio: () -> Unit
) {
    val annotatedBio = remember(bioPayload) {
        BlockFontData.parseBioToAnnotatedString(bioPayload)
    }

    val charLength = bioPayload.length
    val charLimit = 200
    val isOverLimit = charLength > charLimit

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.5.dp,
                if (isOverLimit) NeonPink else NeonCyan,
                RoundedCornerShape(20.dp)
            )
            .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = NeonCyan),
        color = Color(0xFF0F1626)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FREE FIRE PROFILE SIMULATOR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                // Character Counter Gauge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$charLength/$charLimit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverLimit) NeonPink else NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Profile Card Preview Layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070B12))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CyberGradientPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.aryan_profile_avatar),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = profile?.nickname ?: "ARYAN ⚡ VIP",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "UID: ${profile?.accountId ?: "1049281742"}",
                                    fontSize = 10.sp,
                                    color = NeonYellow
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonYellow.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LV. 78",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonYellow
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bio display area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF04060A))
                            .border(0.5.dp, Color(0xFF26334D), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        if (bioPayload.isEmpty()) {
                            Text(
                                text = "Your 3D signature will appear here...",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = annotatedBio,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Copy Bio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onCopyBio,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHigh),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("copy_bio_payload_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copy Raw Bio Payload",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateProfileSection(
    handshakeStep: UpdateHandshakeStep,
    statusMessage: String,
    errorMessage: String?,
    onUpdateClick: () -> Unit
) {
    val isUpdating = handshakeStep in listOf(
        UpdateHandshakeStep.PREPARING_PAYLOAD,
        UpdateHandshakeStep.MAJOR_LOGIN,
        UpdateHandshakeStep.INJECTING_SIGNATURE
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onUpdateClick,
            enabled = !isUpdating,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    if (!isUpdating) CyberGradientPrimary else Brush.linearGradient(listOf(SurfaceCardHigh, SurfaceNight)),
                    RoundedCornerShape(16.dp)
                )
                .border(
                    1.5.dp,
                    if (!isUpdating) NeonCyan else BorderGlass,
                    RoundedCornerShape(16.dp)
                )
                .shadow(if (!isUpdating) 16.dp else 0.dp, RoundedCornerShape(16.dp), spotColor = NeonCyan)
                .testTag("update_profile_bio_main_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = NeonCyan,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "APPLYING BIO...",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Update",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✨ UPDATE FREE FIRE BIO NOW",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }

        if (isUpdating && statusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = statusMessage,
                fontSize = 12.sp,
                color = NeonCyan,
                textAlign = TextAlign.Center
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = NeonPink,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SuccessBioDialog(
    profile: PlayerProfile,
    bioPayload: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_done_button")
            ) {
                Text(
                    text = "Done",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("3D Bio", bioPayload)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied Bio!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardHigh),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dialog_copy_button")
            ) {
                Text(
                    text = "Copy Bio",
                    color = NeonPink,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "BIO UPDATED!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonGreen
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "3D signature applied successfully to Free Fire profile (200 OK)!",
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceNight)
                        .border(1.dp, BorderGlass, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.aryan_profile_avatar),
                            contentDescription = "Player Profile Picture",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = profile.nickname,
                                fontSize = 12.sp,
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "UID: ${profile.accountId}",
                                fontSize = 11.sp,
                                color = NeonYellow
                            )
                            Text(
                                text = "Region: ${profile.region} • Lv.88",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bio Preview snippet in dialog
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF020408))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = BlockFontData.parseBioToAnnotatedString(bioPayload),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Official Telegram & WhatsApp links at the end of bio change
                Text(
                    text = "Join Aryan Community Channels:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPink
                )

                Spacer(modifier = Modifier.height(8.dp))

                CommunityButtonsRow()
            }
        },
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(20.dp)
    )
}
