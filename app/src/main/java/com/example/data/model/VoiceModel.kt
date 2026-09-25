package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

/**
 * Represents a voice option (AI prebuilt model or custom cloned voice).
 */
@JsonClass(generateAdapter = true)
data class VoiceOption(
    val id: String,
    val name: String,
    val gender: String, // "Male", "Female", "Neutral"
    val ageBracket: String = "Young Adult", // "Youth", "Young Adult", "Middle-Aged", "Veteran"
    val accent: String, // "US General", "British (RP)", "Australian", "Indian English", "Irish", "Scottish", "Canadian", "Deep Southern US", "South African"
    val category: String, // "YouTube Pro", "Documentary", "Gaming/Hype", "Explainer", "Storytelling", "True Crime", "Cloned", "ASMR/Calm"
    val description: String,
    val geminiVoiceName: String? = null, // "Puck", "Charon", "Kore", "Fenrir", "Aoede"
    val localeTag: String = "en-US",
    val defaultPitch: Float = 1.0f,
    val defaultRate: Float = 1.0f,
    val recommendedTone: String = "Neutral & Confident",
    val isClone: Boolean = false,
    val clonePersonaPrompt: String = "",
    val timbreColorHex: Long = 0xFF8B5CF6
)

/**
 * Emotional tone preset for voice synthesis modulation.
 */
data class EmotionalToneOption(
    val id: String,
    val name: String,
    val description: String,
    val pitchShift: Float = 1.0f,
    val rateShift: Float = 1.0f,
    val promptDirective: String,
    val iconName: String = "Mood"
)

/**
 * Room Entity for saved YouTube Audio Projects.
 */
@Entity(tableName = "audio_projects")
@JsonClass(generateAdapter = true)
data class AudioProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val scriptText: String,
    val voiceId: String,
    val voiceName: String,
    val voiceAccent: String,
    val emotionalTone: String = "Neutral",
    val audioFilePath: String,
    val durationSeconds: Int,
    val wordCount: Int,
    val estimatedVideoDuration: String,
    val backgroundMusicTrack: String = "None",
    val musicVolumePercent: Int = 20,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val chaptersJson: String = "" // Stored JSON/text of generated YouTube timestamps
)

/**
 * Room Entity for Custom Cloned Voice Personas.
 */
@Entity(tableName = "voice_clones")
@JsonClass(generateAdapter = true)
data class VoiceCloneProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cloneName: String,
    val description: String,
    val referenceAudioPath: String,
    val sampleDurationSeconds: Int = 10,
    val dominantPitchHz: Float = 135f, // Detected pitch
    val speechTempoWpm: Int = 145, // Detected cadence
    val warmthFactor: Float = 0.8f, // EQ warmth
    val resonanceFactor: Float = 0.7f, // Presence
    val clonePromptInstructions: String = "", // Detailed persona instructions for AI synthesis
    val baseVoiceModel: String = "Charon", // Base synthesizer model
    val pitchMultiplier: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val accent: String = "Custom Clone",
    val ageBracket: String = "Young Adult",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Pre-configured YouTube script templates.
 */
data class YouTubeScriptTemplate(
    val id: String,
    val title: String,
    val category: String,
    val targetWpm: Int,
    val estimatedMinutes: String,
    val description: String,
    val iconName: String,
    val sampleScript: String,
    val suggestedVoiceId: String,
    val suggestedTone: String = "High-Energy & Engaging"
)

/**
 * Timestamp marker for YouTube video description chapter markers.
 */
data class VideoChapterMarker(
    val timestampFormatted: String, // "00:00"
    val title: String,
    val characterOffset: Int = 0
)

/**
 * Background ambient audio stem.
 */
data class BackgroundMusicTrack(
    val id: String,
    val name: String,
    val genre: String,
    val tempo: String,
    val mood: String
)
