package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.BackgroundNight
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import kotlin.random.Random

data class Particle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val speed: Float,
    val color: Color,
    val alpha: Float
)

@Composable
fun CyberpunkParticlesBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animProgress"
    )

    val particles = remember {
        val colors = listOf(NeonCyan, NeonPink, NeonPurple, Color(0xFF00FFCC), Color(0xFFFF77A9))
        List(25) {
            Particle(
                xRatio = Random.nextFloat(),
                yRatio = Random.nextFloat(),
                radius = Random.nextFloat() * 12f + 4f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                color = colors.random(),
                alpha = Random.nextFloat() * 0.4f + 0.15f
            )
        }
    }

    val baseGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                BackgroundNight,
                Color(0xFF0F1522),
                Color(0xFF080C14)
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Base gradient background
        drawRect(
            brush = baseGradient,
            size = size
        )

        // Draw ambient glow halos
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonPink.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(width * 0.2f, height * 0.15f),
                radius = width * 0.5f
            ),
            radius = width * 0.5f,
            center = Offset(width * 0.2f, height * 0.15f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonCyan.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(width * 0.85f, height * 0.75f),
                radius = width * 0.6f
            ),
            radius = width * 0.6f,
            center = Offset(width * 0.85f, height * 0.75f)
        )

        // Draw drifting glowing particles
        particles.forEach { p ->
            val curY = ((p.yRatio - animProgress * p.speed) % 1f + 1f) % 1f * height
            val curX = (p.xRatio + kotlin.math.sin((animProgress * 6.28 + p.yRatio * 10).toDouble()).toFloat() * 0.03f) * width

            drawCircle(
                color = p.color.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(curX, curY)
            )

            // Outer soft glow
            drawCircle(
                color = p.color.copy(alpha = p.alpha * 0.35f),
                radius = p.radius * 2.2f,
                center = Offset(curX, curY)
            )
        }
    }
}
