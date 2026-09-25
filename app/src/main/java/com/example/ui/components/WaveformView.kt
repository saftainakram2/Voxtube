package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCoral

@Composable
fun WaveformView(
    amplitudes: List<Float>,
    currentPositionMs: Int,
    durationMs: Int,
    isPlaying: Boolean,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = if (durationMs > 0) {
        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

    // Animated glow shimmer when playing
    val infiniteTransition = rememberInfiniteTransition(label = "waveGlow")
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glowPhase"
    )

    val bars = if (amplitudes.isNotEmpty()) amplitudes else List(40) { 0.35f }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            }
    ) {
        val totalWidth = size.width
        val totalHeight = size.height
        val barCount = bars.size
        val barSpacing = 4.dp.toPx()
        val totalSpacing = barSpacing * (barCount - 1)
        val barWidth = ((totalWidth - totalSpacing) / barCount).coerceAtLeast(3.dp.toPx())

        val playedBrush = Brush.verticalGradient(
            colors = listOf(NeonCyan, ElectricPurple)
        )
        val unplayedBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF332D48), Color(0xFF221C34))
        )
        val activeBarBrush = Brush.verticalGradient(
            colors = listOf(NeonCoral, ElectricPurple)
        )

        val progressX = totalWidth * progressFraction

        for (i in 0 until barCount) {
            val barAmp = bars[i]
            val x = i * (barWidth + barSpacing)
            val barHeight = (totalHeight * barAmp).coerceAtLeast(6.dp.toPx())
            val y = (totalHeight - barHeight) / 2

            val isPlayed = (x + barWidth) <= progressX
            val isCurrentBar = progressX in x..(x + barWidth + barSpacing)

            val brush = when {
                isCurrentBar && isPlaying -> activeBarBrush
                isPlayed -> playedBrush
                else -> unplayedBrush
            }

            drawRoundRect(
                brush = brush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }

        // Current scrubber head line
        if (progressX > 0) {
            drawLine(
                color = NeonCyan,
                start = Offset(progressX, 0f),
                end = Offset(progressX, totalHeight),
                strokeWidth = 2.5.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(progressX, totalHeight / 2)
            )
        }
    }
}
