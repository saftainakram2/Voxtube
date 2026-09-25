package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan

@Composable
fun YouTubePacingMeter(
    wordCount: Int,
    characterCount: Int,
    estimatedDuration: String,
    speechRateMultiplier: Float,
    modifier: Modifier = Modifier
) {
    val estimatedSeconds = parseSeconds(estimatedDuration)
    val isShortsOptimized = estimatedSeconds in 15..58
    val effectiveWpm = (140 * speechRateMultiplier).toInt()

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF141022),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word count item
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "WORDS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "$wordCount",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Pacing Speed (WPM)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PACING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
                Text(
                    text = "$effectiveWpm WPM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonCyan
                )
            }

            // Estimated Video Runtime
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = if (isShortsOptimized) NeonCoral else ElectricPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isShortsOptimized) "SHORTS READY" else "EST. VIDEO RUNTIME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isShortsOptimized) NeonCoral else Color(0xFF64748B)
                    )
                }
                Text(
                    text = estimatedDuration,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isShortsOptimized) NeonCoral else GoldAccent,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

private fun parseSeconds(durationStr: String): Int {
    return try {
        val parts = durationStr.split(":")
        val min = parts[0].toInt()
        val sec = parts[1].toInt()
        min * 60 + sec
    } catch (e: Exception) {
        0
    }
}
