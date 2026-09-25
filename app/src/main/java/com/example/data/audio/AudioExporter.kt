package com.example.data.audio

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.AudioProject
import com.example.data.model.VideoChapterMarker
import java.io.File

object AudioExporter {

    /**
     * Shares audio file via Android share sheet to video editors / external storage.
     */
    fun shareAudioFile(context: Context, project: AudioProject): Boolean {
        val file = File(project.audioFilePath)
        if (!file.exists()) return false

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "VoxTube: ${project.title}")
            putExtra(
                Intent.EXTRA_TEXT,
                "Audio voiceover for YouTube: \"${project.title}\"\nVoice: ${project.voiceName} (${project.voiceAccent})\nDuration: ${project.estimatedVideoDuration}"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Export Voiceover to Video Editor"))
        return true
    }

    /**
     * Generates YouTube Chapter Timestamps for the video description.
     */
    fun generateYouTubeTimestamps(scriptText: String, totalDurationSeconds: Int): List<VideoChapterMarker> {
        val paragraphs = scriptText.split("\n\n").map { it.trim() }.filter { it.isNotBlank() }
        if (paragraphs.isEmpty()) {
            return listOf(VideoChapterMarker("00:00", "Introduction"))
        }

        val totalWords = scriptText.split("\\s+".toRegex()).size.coerceAtLeast(1)
        val markers = mutableListOf<VideoChapterMarker>()
        markers.add(VideoChapterMarker("00:00", "Introduction"))

        var accumulatedWords = 0
        for ((index, paragraph) in paragraphs.withIndex()) {
            val wordsInPara = paragraph.split("\\s+".toRegex()).size
            accumulatedWords += wordsInPara

            if (index > 0 && accumulatedWords < totalWords) {
                val timeFraction = accumulatedWords.toFloat() / totalWords.toFloat()
                val currentSeconds = (timeFraction * totalDurationSeconds).toInt()
                val minutes = currentSeconds / 60
                val seconds = currentSeconds % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)

                // Extract short summary or first words
                val cleanLine = paragraph.replace("\\[.*?\\]".toRegex(), "").trim()
                val title = if (cleanLine.contains(":")) {
                    cleanLine.substringBefore(":").take(30).trim()
                } else {
                    cleanLine.split(".").firstOrNull()?.take(28)?.trim() ?: "Chapter ${index + 1}"
                }

                if (title.isNotBlank()) {
                    markers.add(VideoChapterMarker(formattedTime, title))
                }
            }
        }

        return markers.distinctBy { it.timestampFormatted }
    }

    /**
     * Formats chapter markers as ready-to-paste YouTube description block.
     */
    fun formatChaptersForYouTube(markers: List<VideoChapterMarker>): String {
        return buildString {
            append("⏱️ Timestamps:\n")
            markers.forEach { marker ->
                append("${marker.timestampFormatted} - ${marker.title}\n")
            }
        }
    }
}
