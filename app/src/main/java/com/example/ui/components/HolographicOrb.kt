package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import kotlin.math.cos
import kotlin.math.sin

enum class OrbState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ALERT
}

@Composable
fun HolographicOrb(
    state: OrbState,
    rmsLevel: Float = 0f,
    size: Dp = 190.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbTransitions")

    // Rotation for outer turbine rings
    val rotationFast by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotationFast"
    )

    val rotationSlow by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotationSlow"
    )

    // Breathing pulse
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == OrbState.LISTENING || state == OrbState.SPEAKING) 700 else 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbPulse"
    )

    // Determine state colors
    val (primaryGlow, accentGlow, coreColor) = when (state) {
        OrbState.LISTENING -> Triple(NeonGreen, NeonCyan, Color(0xFFE0FFEF))
        OrbState.PROCESSING -> Triple(NeonAmber, NeonCrimson, Color(0xFFFFF6D6))
        OrbState.SPEAKING -> Triple(NeonCyan, Color(0xFF9D4EDD), Color(0xFFE3F8FF))
        OrbState.ALERT -> Triple(NeonCrimson, NeonAmber, Color(0xFFFFD6E0))
        OrbState.IDLE -> Triple(NeonCyan, Color(0xFF1E88E5), Color(0xFFE6FBFF))
    }

    Box(
        modifier = Modifier
            .size(size)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val radius = (size.toPx() / 2f) * 0.88f
            val dynamicScale = (pulse + rmsLevel * 0.4f).coerceIn(0.85f, 1.4f)

            // 1. Ambient Outer Radial Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryGlow.copy(alpha = 0.28f * dynamicScale), Color.Transparent),
                    center = center,
                    radius = radius * 1.15f
                ),
                center = center,
                radius = radius * 1.15f
            )

            // 2. Slow Counter-Rotating Outer Bracket Ring
            rotate(rotationSlow, pivot = center) {
                drawCircle(
                    color = primaryGlow.copy(alpha = 0.35f),
                    radius = radius * 0.95f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Outer HUD nodes (8 satellite dots)
                for (i in 0 until 8) {
                    val angle = (i * 45.0) * (Math.PI / 180.0)
                    val nodePos = Offset(
                        (center.x + radius * 0.95f * cos(angle)).toFloat(),
                        (center.y + radius * 0.95f * sin(angle)).toFloat()
                    )
                    drawCircle(
                        color = accentGlow,
                        radius = 2.5.dp.toPx(),
                        center = nodePos
                    )
                }
            }

            // 3. Fast Clockwise Arc Ring (Turbine style)
            rotate(rotationFast, pivot = center) {
                for (i in 0 until 6) {
                    val startAngle = i * 60f + 10f
                    drawArc(
                        color = primaryGlow.copy(alpha = 0.85f),
                        startAngle = startAngle,
                        sweepAngle = 38f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius * 0.78f, center.y - radius * 0.78f),
                        size = androidx.compose.ui.geometry.Size(radius * 1.56f, radius * 1.56f),
                        style = Stroke(width = 3.2.dp.toPx())
                    )
                }
            }

            // 4. Reactive Equalizer / Audio Waveform Circle
            val rayCount = 18
            val baseRayRadius = radius * 0.62f
            for (i in 0 until rayCount) {
                val angle = (i * (360.0 / rayCount)) * (Math.PI / 180.0)
                val rayLength = if (state == OrbState.SPEAKING || state == OrbState.LISTENING) {
                    (sin(rotationFast * 0.1f + i * 0.8f) * 12f + rmsLevel * 28f + 8f).coerceAtLeast(4f)
                } else {
                    4f
                }
                val start = Offset(
                    (center.x + baseRayRadius * cos(angle)).toFloat(),
                    (center.y + baseRayRadius * sin(angle)).toFloat()
                )
                val end = Offset(
                    (center.x + (baseRayRadius + rayLength) * cos(angle)).toFloat(),
                    (center.y + (baseRayRadius + rayLength) * sin(angle)).toFloat()
                )
                drawLine(
                    color = accentGlow.copy(alpha = 0.75f),
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx()
                )
            }

            // 5. Pulsing Arc Reactor Core
            val coreRadius = (radius * 0.44f * dynamicScale).coerceAtLeast(15.dp.toPx())
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(coreColor, primaryGlow, Color.Transparent),
                    center = center,
                    radius = coreRadius
                ),
                center = center,
                radius = coreRadius
            )

            // Inner Core Sharp Ring
            drawCircle(
                color = Color.White,
                radius = coreRadius * 0.35f,
                center = center
            )
        }
    }
}
