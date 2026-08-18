package com.example.ui.components

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.BlockFontData
import com.example.network.PlayerProfile
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

enum class ProfileCardTheme(
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val bannerGradient: List<Color>,
    val cardBackground: Color
) {
    CYBER_TITAN(
        title = "Cyberpunk Neo",
        primaryColor = Color(0xFF00F2FE),
        secondaryColor = Color(0xFFFF0844),
        bannerGradient = listOf(Color(0xFF0B192C), Color(0xFF1E3E62), Color(0xFF000000)),
        cardBackground = Color(0xFF0B111D)
    ),
    GRANDMASTER_ROYALE(
        title = "Grandmaster Gold",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFFF4500),
        bannerGradient = listOf(Color(0xFF2C1600), Color(0xFF5A2E00), Color(0xFF150A00)),
        cardBackground = Color(0xFF130E09)
    ),
    CRIMSON_SHADOW(
        title = "Crimson Blade",
        primaryColor = Color(0xFFFF0055),
        secondaryColor = Color(0xFF8A0035),
        bannerGradient = listOf(Color(0xFF2A0815), Color(0xFF540D2B), Color(0xFF100308)),
        cardBackground = Color(0xFF14080D)
    ),
    EMERALD_MATRIX(
        title = "Matrix Green",
        primaryColor = Color(0xFF00FF66),
        secondaryColor = Color(0xFF00A86B),
        bannerGradient = listOf(Color(0xFF052212), Color(0xFF0E4D2B), Color(0xFF021008)),
        cardBackground = Color(0xFF06140D)
    )
}

/**
 * Real-time In-Game Free Fire Profile Card Preview Component.
 * Dynamically displays the avatar, custom titles, rank badges, and formatted bio in real-time.
 */
@Composable
fun FullProfileCardPreview(
    profile: PlayerProfile?,
    bioPayload: String,
    modifier: Modifier = Modifier,
    onCopyBio: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTheme by remember { mutableStateOf(ProfileCardTheme.CYBER_TITAN) }
    var isFullscreenModalOpen by remember { mutableStateOf(false) }

    val annotatedBio = remember(bioPayload) {
        BlockFontData.parseBioToAnnotatedString(bioPayload)
    }

    val charLength = bioPayload.length
    val charLimit = 200
    val isOverLimit = charLength > charLimit

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAnim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                1.5.dp,
                if (isOverLimit) NeonPink else selectedTheme.primaryColor.copy(alpha = 0.7f),
                RoundedCornerShape(22.dp)
            )
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = if (isOverLimit) NeonPink else selectedTheme.primaryColor
            )
            .testTag("real_time_profile_card_preview"),
        colors = CardDefaults.cardColors(containerColor = selectedTheme.cardBackground),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Bar with Simulator Status & Theme Switcher
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
                        text = "REAL-TIME PROFILE CARD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Character Gauge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isOverLimit) NeonPink.copy(alpha = 0.2f) else selectedTheme.primaryColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$charLength/$charLimit",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOverLimit) NeonPink else selectedTheme.primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { isFullscreenModalOpen = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen Preview",
                            tint = selectedTheme.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Free Fire In-Game Profile Card Design
            InGameCardContent(
                profile = profile,
                bioPayload = bioPayload,
                annotatedBio = annotatedBio,
                theme = selectedTheme,
                glowAlpha = glowAnim,
                onCopyUid = {
                    val uid = profile?.accountId ?: "1049281742"
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("UID", uid))
                    Toast.makeText(context, "UID $uid Copied!", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Theme Switcher & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Banner Theme Dots
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Banner:",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    ProfileCardTheme.values().forEach { theme ->
                        val isSelected = selectedTheme == theme
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(theme.primaryColor)
                                .border(
                                    if (isSelected) 2.dp else 0.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { selectedTheme = theme }
                        )
                    }
                }

                // Copy Bio Payload Button
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Free Fire Bio", bioPayload)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Bio Payload Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                        onCopyBio()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedTheme.primaryColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("copy_bio_payload_action")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color.Black,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copy Bio",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    // Fullscreen Card Inspection Modal
    if (isFullscreenModalOpen) {
        Dialog(
            onDismissRequest = { isFullscreenModalOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(selectedTheme.cardBackground)
                        .border(2.dp, selectedTheme.primaryColor, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OFFICIAL PROFILE CARD SHOWCASE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = selectedTheme.primaryColor,
                            letterSpacing = 1.sp
                        )
                        IconButton(onClick = { isFullscreenModalOpen = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    InGameCardContent(
                        profile = profile,
                        bioPayload = bioPayload,
                        annotatedBio = annotatedBio,
                        theme = selectedTheme,
                        glowAlpha = glowAnim,
                        onCopyUid = {
                            val uid = profile?.accountId ?: "1049281742"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("UID", uid))
                            Toast.makeText(context, "UID $uid Copied!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Free Fire Bio", bioPayload)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Bio Payload Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                            isFullscreenModalOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = selectedTheme.primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COPY BIO PAYLOAD & CLOSE",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Authentic In-Game Free Fire Profile Card Layout Content
 */
@Composable
private fun InGameCardContent(
    profile: PlayerProfile?,
    bioPayload: String,
    annotatedBio: androidx.compose.ui.text.AnnotatedString,
    theme: ProfileCardTheme,
    glowAlpha: Float,
    onCopyUid: () -> Unit
) {
    val playerName = profile?.nickname ?: "ARYAN ⚡ MX EDITZ"
    val playerUid = profile?.accountId ?: "1049281742"
    val playerRegion = profile?.region ?: "BD / SG / IND"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = theme.bannerGradient
                )
            )
            .border(1.dp, Color(0xFF263A50), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Avatar + Name + Badges + Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Avatar with glowing ring & Level badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Glowing Outer Ring
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(
                                    BorderStroke(
                                        2.dp,
                                        Brush.sweepGradient(
                                            listOf(
                                                theme.primaryColor,
                                                theme.secondaryColor,
                                                theme.primaryColor
                                            )
                                        )
                                    ),
                                    CircleShape
                                )
                                .shadow(8.dp, CircleShape, spotColor = theme.primaryColor)
                        )

                        // Avatar Image (Aryan MX Editz)
                        Image(
                            painter = painterResource(id = R.drawable.aryan_profile_avatar),
                            contentDescription = "Player Profile Avatar",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                        )

                        // Level Badge Over Avatar
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF000000).copy(alpha = 0.85f))
                                .border(0.5.dp, theme.primaryColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Lv.88",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.primaryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Player Name & Identity Badges
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = playerName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "VIP Verified",
                                tint = NeonGreen,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // UID & Guild Row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "UID: $playerUid",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonYellow
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy UID",
                                tint = TextMuted,
                                modifier = Modifier
                                    .size(11.dp)
                                    .clickable { onCopyUid() }
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Guild Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "亗 MX EDITZ BD 亗",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primaryColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = playerRegion,
                                    fontSize = 8.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Right: Likes & Battle Honors
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF0844).copy(alpha = 0.18f))
                            .border(0.5.dp, Color(0xFFFF0844).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Likes",
                                tint = Color(0xFFFF0844),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "148.9K",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HONOR: 100",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Rank Badges Showcase
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF05080E).copy(alpha = 0.7f))
                    .border(0.5.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BR-Ranked
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "BR Rank",
                        tint = NeonYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "BR-RANKED",
                            fontSize = 8.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grandmaster (8,920)",
                            fontSize = 9.sp,
                            color = NeonYellow,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // CS-Ranked
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "CS Rank",
                        tint = theme.primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "CS-RANKED",
                            fontSize = 8.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Master ★72",
                            fontSize = 9.sp,
                            color = theme.primaryColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Booyah Pass
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Booyah Pass",
                        tint = NeonPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "BOOYAH PASS",
                            fontSize = 8.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Lv. 250 (Max)",
                            fontSize = 9.sp,
                            color = NeonPink,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // In-Game Signature / Bio Display Box (Live Rendered with real-time styling)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF020408).copy(alpha = 0.9f))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SIGNATURE / BIO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "FREE FIRE LIVE RENDER",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (bioPayload.isEmpty()) {
                        Text(
                            text = "Your custom 3D signature and gradient tags will render live here in real-time...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
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
    }
}
