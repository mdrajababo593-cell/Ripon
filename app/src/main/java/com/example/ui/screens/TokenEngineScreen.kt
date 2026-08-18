package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storage.StorageManager
import com.example.ui.components.AryanHeroBanner
import com.example.ui.components.CommunityCardEndBioChange
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.BorderPink
import com.example.ui.theme.CyberGlowCyan
import com.example.ui.theme.CyberGlowPink
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
import com.example.viewmodel.StudioUiState
import com.example.viewmodel.StudioViewModel

@Composable
fun TokenEngineScreen(
    state: StudioUiState,
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aryan Hero Banner & Profile Avatar
        AryanHeroBanner(
            showCommunityButtons = true,
            modifier = Modifier.testTag("aryan_hero_banner")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Free 1-Tap Entrance Button
        Button(
            onClick = { viewModel.enterFreeBioStudio() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(CyberGradientPink, RoundedCornerShape(16.dp))
                .border(1.5.dp, NeonYellow, RoundedCornerShape(16.dp))
                .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = NeonPink)
                .testTag("enter_free_bio_studio_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Enter Studio",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FREE 3D BIO & FONT CUSTOMIZER",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Real-Time In-Game Profile Card Preview
        com.example.ui.components.FullProfileCardPreview(
            profile = state.verifiedProfile,
            bioPayload = state.finalBioPayload.ifEmpty { "[b][c][FF0844]█▀█[00FFFF]█░█[00FF00]█▀█[FFD700]█▀█[FF00FF]█▄█[00F2FE]█░█\n[b][c][FF0055]亗 [00FFFF]MX EDITZ BD [FF0055]亗\n[b][c][FFD700]★ [FFFFFF]HEADSHOT GOD [FFD700]★" },
            onCopyBio = {
                val payload = state.finalBioPayload.ifEmpty { "[b][c][FF0844]█▀█[00FFFF]█░█[00FF00]█▀█[FFD700]█▀█[FF00FF]█▄█[00F2FE]█░█\n[b][c][FF0055]亗 [00FFFF]MX EDITZ BD [FF0055]亗\n[b][c][FFD700]★ [FFFFFF]HEADSHOT GOD [FFD700]★" }
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Free Fire 3D Bio", payload)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "3D Bio copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Storage Permission Banner
        StoragePermissionBanner(
            hasPermission = state.hasStoragePermission,
            onGrantClick = {
                val intent = StorageManager.getStoragePermissionIntent(context)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not open settings: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onRefreshClick = {
                viewModel.checkStoragePermission()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Method A: VPN-Style Auto-Capture Switch
        AutoCaptureCard(
            isRunning = state.isAutoCaptureRunning,
            status = state.autoCaptureStatus,
            onToggle = { enable ->
                viewModel.toggleAutoCapture(enable)
            },
            onExportConfig = {
                val file = viewModel.exportConfigFile(context)
                if (file != null) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("localconfig.json", StorageManager.LOCAL_CONFIG_JSON)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        "Exported & JSON copied to clipboard! (Place in Android/data/com.dts.freefireth/files/)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BorderGlass)
            )
            Text(
                text = "  OR CONNECT WITH TOKEN  ",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BorderGlass)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Method B: Manual Token Input Card
        ManualTokenCard(
            token = state.manualTokenInput,
            isMasked = state.isTokenMasked,
            isLoading = state.isVerifyingToken,
            errorMessage = state.tokenErrorMessage,
            onTokenChange = { viewModel.setManualToken(it) },
            onToggleMask = { viewModel.toggleTokenMask() },
            onVerify = { viewModel.verifyAndProceed() },
            onDemoClick = { viewModel.loadSampleDemoAccount() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Community Card
        CommunityCardEndBioChange(
            modifier = Modifier.testTag("footer_community_card")
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StoragePermissionBanner(
    hasPermission: Boolean,
    onGrantClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (hasPermission) NeonGreen.copy(alpha = 0.5f) else NeonYellow.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            ),
        color = SurfaceGlass
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (hasPermission) NeonGreen.copy(alpha = 0.15f) else NeonYellow.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasPermission) Icons.Default.CheckCircle else Icons.Default.Shield,
                        contentDescription = "Permission Status",
                        tint = if (hasPermission) NeonGreen else NeonYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (hasPermission) "Storage Access Active" else "Storage Access Needed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasPermission) NeonGreen else NeonYellow
                    )
                    Text(
                        text = if (hasPermission) "Auto-deploy to /Android/data supported" else "Grant to auto-deploy localconfig.json",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!hasPermission) {
                    Button(
                        onClick = onGrantClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonYellow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("grant_permission_button")
                    ) {
                        Text(
                            text = "Grant",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                IconButton(onClick = onRefreshClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Permission",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoCaptureCard(
    isRunning: Boolean,
    status: String,
    onToggle: (Boolean) -> Unit,
    onExportConfig: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarPulse"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.5.dp,
                if (isRunning) NeonGreen else BorderGlass,
                RoundedCornerShape(20.dp)
            )
            .shadow(if (isRunning) 16.dp else 4.dp, RoundedCornerShape(20.dp), spotColor = NeonGreen),
        color = SurfaceCard
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "1-TAP AUTO CAPTURE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Localhost Interceptor (127.0.0.1:6677)",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isRunning) NeonGreen.copy(alpha = 0.2f) else SurfaceNight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isRunning) "ACTIVE" else "STANDBY",
                        color = if (isRunning) NeonGreen else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Radar Visualizer & Power Button
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clickable { onToggle(!isRunning) },
                contentAlignment = Alignment.Center
            ) {
                if (isRunning) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val radius = size.minDimension / 2f
                        drawCircle(
                            color = NeonGreen.copy(alpha = (1f - radarPulse) * 0.5f),
                            radius = radius * radarPulse,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawCircle(
                            color = NeonCyan.copy(alpha = (1f - (radarPulse + 0.5f) % 1f) * 0.4f),
                            radius = radius * ((radarPulse + 0.5f) % 1f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            if (isRunning) CyberGradientPrimary else Brush.radialGradient(
                                listOf(SurfaceCardHigh, SurfaceNight)
                            )
                        )
                        .border(
                            2.dp,
                            if (isRunning) NeonCyan else BorderGlass,
                            CircleShape
                        )
                        .shadow(if (isRunning) 20.dp else 0.dp, CircleShape, spotColor = NeonCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Toggle Listener",
                        tint = if (isRunning) Color.White else TextSecondary,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isRunning) "Tap to Stop Interceptor" else "Tap to Launch Auto-Capture",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isRunning) NeonGreen else TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = status,
                fontSize = 11.sp,
                color = if (isRunning) NeonCyan else TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Export / Manual fallback button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onExportConfig() }
                    .border(1.dp, BorderGlass, RoundedCornerShape(12.dp)),
                color = SurfaceNight
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export Config",
                        tint = NeonPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Export localconfig.json & Copy Path",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualTokenCard(
    token: String,
    isMasked: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onTokenChange: (String) -> Unit,
    onToggleMask: () -> Unit,
    onVerify: () -> Unit,
    onDemoClick: () -> Unit
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MANUAL CREDENTIALS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = TextPrimary
                )

                Text(
                    text = "Demo Token",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPink,
                    modifier = Modifier
                        .clickable { onDemoClick() }
                        .padding(4.dp)
                        .testTag("load_demo_account_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Paste your Access Token or Guest credentials (UID:Password)",
                fontSize = 11.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = token,
                onValueChange = onTokenChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_token_input_field"),
                placeholder = {
                    Text(
                        text = "Paste AccessToken or UID:Password...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                singleLine = false,
                maxLines = 3,
                visualTransformation = if (isMasked) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = onToggleMask) {
                        Icon(
                            imageVector = if (isMasked) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Mask",
                            tint = NeonCyan
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = BorderGlass,
                    focusedContainerColor = SurfaceNight,
                    unfocusedContainerColor = SurfaceNight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onVerify() })
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = NeonPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        color = NeonPink
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVerify,
                enabled = !isLoading && token.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    disabledContainerColor = SurfaceCardHigh
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("verify_and_connect_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Connect",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONNECT & CUSTOMIZE",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
