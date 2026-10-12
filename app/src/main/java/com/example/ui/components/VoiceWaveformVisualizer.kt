package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen

@Composable
fun VoiceWaveformVisualizer(
    isActive: Boolean,
    rmsLevel: Float = 0f,
    modifier: Modifier = Modifier,
    barCount: Int = 24,
    height: Dp = 36.dp,
    activeColor: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformBars")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveformPhase"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val normalizedIdx = (i - barCount / 2).toFloat() / (barCount / 2).toFloat()
            val curveFactor = (1f - normalizedIdx * normalizedIdx).coerceIn(0.2f, 1f)

            val animatedHeight = if (isActive) {
                val wave = kotlin.math.sin(phase + i * 0.35f) * 0.4f + 0.6f
                ((wave * curveFactor * 0.6f + rmsLevel * 0.4f) * height.value).coerceIn(4f, height.value)
            } else {
                3f
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(animatedHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = if (isActive) {
                                listOf(activeColor, NeonGreen)
                            } else {
                                listOf(Color(0xFF1E3355), Color(0xFF0F1E33))
                            }
                        )
                    )
            )
            if (i < barCount - 1) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}
