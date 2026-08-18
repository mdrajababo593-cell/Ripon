package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Cute Cyberpunk & Neon Glassmorphism Palette
val BackgroundNight = Color(0xFF0B0E14)
val SurfaceNight = Color(0xFF111723)
val SurfaceCard = Color(0xFF171F30)
val SurfaceCardHigh = Color(0xFF1F293F)
val SurfaceGlass = Color(0xCC151C2C)

val NeonCyan = Color(0xFF00F2FE)
val NeonCyanDim = Color(0xFF00B4D8)
val NeonPink = Color(0xFFFF0844)
val NeonPinkDim = Color(0xFFD80032)
val NeonPurple = Color(0xFF7F00FF)
val NeonPurpleLight = Color(0xFFA855F7)
val NeonGreen = Color(0xFF00FF66)
val NeonYellow = Color(0xFFFFD700)
val NeonOrange = Color(0xFFFF7700)

val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val BorderGlass = Color(0x3300F2FE)
val BorderPink = Color(0x33FF0844)
val BorderHighlight = Color(0x5500F2FE)

val CyberGlowCyan = Color(0x4000F2FE)
val CyberGlowPink = Color(0x40FF0844)

val CyberGradientPrimary = Brush.horizontalGradient(
    colors = listOf(NeonPink, NeonPurple, NeonCyan)
)

val CyberGradientCyan = Brush.horizontalGradient(
    colors = listOf(NeonCyan, Color(0xFF4FACFE))
)

val CyberGradientPink = Brush.horizontalGradient(
    colors = listOf(NeonPink, Color(0xFFFF4E50))
)

val CyberGradientGreen = Brush.horizontalGradient(
    colors = listOf(Color(0xFF00F260), Color(0xFF0575E6))
)
