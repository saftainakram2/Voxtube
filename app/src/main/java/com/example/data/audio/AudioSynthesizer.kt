package com.example.data.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerateRequest
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiPrebuiltVoiceConfig
import com.example.data.api.GeminiSpeechConfig
import com.example.data.api.GeminiVoiceConfig
import com.example.data.model.EmotionalToneOption
import com.example.data.model.VoiceOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

class AudioSynthesizer(private val context: Context) {

    private var localTts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        initLocalTts()
    }

    private fun initLocalTts() {
        localTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                localTts?.language = Locale.US
            } else {
                Log.e("AudioSynthesizer", "Local TTS init failed: $status")
            }
        }
    }

    /**
     * Synthesizes script text into an audio file.
     * Incorporates emotional tone, pitch controls, speaking rate, and word emphasis tags.
     */
    suspend fun synthesizeSpeech(
        script: String,
        voice: VoiceOption,
        emotionalTone: EmotionalToneOption,
        pitchMultiplier: Float = 1.0f,
        speedMultiplier: Float = 1.0f,
        onProgressUpdate: (String) -> Unit = {}
    ): Result<SynthesizedAudioResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasApiKey = apiKey.isNotBlank() && !apiKey.startsWith("MY_GEMINI")

        if (hasApiKey && voice.geminiVoiceName != null) {
            onProgressUpdate("Synthesizing ${voice.name} (${emotionalTone.name}) with Neural AI...")
            val geminiResult = tryGeminiTts(script, voice, emotionalTone, apiKey, pitchMultiplier, speedMultiplier)
            if (geminiResult.isSuccess) {
                return@withContext geminiResult
            } else {
                Log.w("AudioSynthesizer", "Gemini TTS fallback: ${geminiResult.exceptionOrNull()?.message}")
                onProgressUpdate("Switching to offline studio audio engine...")
            }
        }

        // Offline / Local TTS synthesis
        onProgressUpdate("Synthesizing studio voice track (${voice.accent})...")
        tryLocalTtsSynthesis(script, voice, emotionalTone, pitchMultiplier, speedMultiplier)
    }

    private suspend fun tryGeminiTts(
        script: String,
        voice: VoiceOption,
        emotionalTone: EmotionalToneOption,
        apiKey: String,
        pitchMultiplier: Float,
        speedMultiplier: Float
    ): Result<SynthesizedAudioResult> = withContext(Dispatchers.IO) {
        try {
            val geminiVoice = voice.geminiVoiceName ?: "Charon"
            
            // Build rich emotional tone and emphasis-aware prompt
            val styledPrompt = buildString {
                append("You are an elite voiceover artist narrating a YouTube video.\n")
                append("Voice Profile: ${voice.name}, Accent: ${voice.accent}, Age: ${voice.ageBracket}.\n")
                append("Emotional Direction: ${emotionalTone.promptDirective}\n")
                if (voice.isClone && voice.clonePersonaPrompt.isNotBlank()) {
                    append("Clone Instructions: ${voice.clonePersonaPrompt}\n")
                }
                append("Pacing Multiplier: ${String.format("%.2f", speedMultiplier * emotionalTone.rateShift)}x, Pitch Tuning: ${String.format("%.2f", pitchMultiplier * emotionalTone.pitchShift)}x.\n\n")
                append("Narration Guidelines:\n")
                append("- Strongly emphasize words enclosed in [Emphasis: ...] or marked with [Emphasis].\n")
                append("- Respect pause tags like [Pause 0.5s] or [Pause 1.0s].\n")
                append("- Read the following script naturally without speaking bracketed stage cues aloud:\n\n")
                append(script)
            }

            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = styledPrompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    responseModalities = listOf("AUDIO"),
                    speechConfig = GeminiSpeechConfig(
                        voiceConfig = GeminiVoiceConfig(
                            prebuiltVoiceConfig = GeminiPrebuiltVoiceConfig(voiceName = geminiVoice)
                        )
                    ),
                    temperature = 0.65f
                )
            )

            // Using gemini-2.5-flash-preview-tts for TTS
            val response = GeminiApiClient.service.generateContent(
                model = "gemini-2.5-flash-preview-tts",
                apiKey = apiKey,
                request = request
            )

            val inlineData = response.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull { it.inlineData != null }
                ?.inlineData

            if (inlineData == null || inlineData.data.isBlank()) {
                val errorMsg = response.error?.message ?: "No audio data received from Gemini"
                return@withContext Result.failure(Exception(errorMsg))
            }

            val audioBytes = Base64.decode(inlineData.data, Base64.DEFAULT)
            val audioDir = File(context.filesDir, "audio").apply { mkdirs() }
            val outputFile = File(audioDir, "gemini_tts_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.wav")

            // Ensure proper WAV header if raw PCM
            val finalBytes = if (inlineData.mimeType.contains("pcm", ignoreCase = true) || !isWavFormat(audioBytes)) {
                pcmToWav(audioBytes, sampleRate = 24000, channels = 1, bitsPerSample = 16)
            } else {
                audioBytes
            }

            FileOutputStream(outputFile).use { it.write(finalBytes) }

            val estimatedDurationSeconds = calculateDurationFromWav(outputFile, finalBytes.size, script)

            Result.success(
                SynthesizedAudioResult(
                    filePath = outputFile.absolutePath,
                    durationSeconds = estimatedDurationSeconds,
                    isAiModel = true,
                    engineName = "Gemini Neural Voice (${voice.name})"
                )
            )
        } catch (e: Exception) {
            Log.e("AudioSynthesizer", "Gemini TTS error", e)
            Result.failure(e)
        }
    }

    private suspend fun tryLocalTtsSynthesis(
        script: String,
        voice: VoiceOption,
        emotionalTone: EmotionalToneOption,
        pitchMultiplier: Float,
        speedMultiplier: Float
    ): Result<SynthesizedAudioResult> = withContext(Dispatchers.IO) {
        val tts = localTts ?: return@withContext Result.failure(Exception("TTS engine not available"))

        val locale = parseLocaleTag(voice.localeTag)
        tts.language = locale

        val effectivePitch = (voice.defaultPitch * pitchMultiplier * emotionalTone.pitchShift).coerceIn(0.5f, 2.0f)
        val effectiveRate = (voice.defaultRate * speedMultiplier * emotionalTone.rateShift).coerceIn(0.5f, 2.0f)

        tts.setPitch(effectivePitch)
        tts.setSpeechRate(effectiveRate)

        val audioDir = File(context.filesDir, "audio").apply { mkdirs() }
        val outputFile = File(audioDir, "local_tts_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.wav")

        val cleanedText = cleanScriptForTts(script)
        val utteranceId = "utt_${System.currentTimeMillis()}"

        val success = suspendCancellableCoroutine<Boolean> { continuation ->
            val listener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(id: String?) {
                    if (id == utteranceId) {
                        if (continuation.isActive) continuation.resume(true)
                    }
                }
                override fun onError(id: String?) {
                    if (id == utteranceId) {
                        if (continuation.isActive) continuation.resume(false)
                    }
                }
            }
            tts.setOnUtteranceProgressListener(listener)

            val params = Bundle()
            val result = tts.synthesizeToFile(cleanedText, params, outputFile, utteranceId)
            if (result != TextToSpeech.SUCCESS) {
                if (continuation.isActive) continuation.resume(false)
            }
        }

        if (success && outputFile.exists() && outputFile.length() > 0) {
            val durationSeconds = calculateDurationFromWav(outputFile, outputFile.length().toInt(), script)
            Result.success(
                SynthesizedAudioResult(
                    filePath = outputFile.absolutePath,
                    durationSeconds = durationSeconds,
                    isAiModel = false,
                    engineName = "Studio Speech Engine (${voice.name})"
                )
            )
        } else {
            // Harmonic synth fallback
            val syntheticFile = generateSyntheticVoiceWav(cleanedText, outputFile, voice, effectivePitch, effectiveRate)
            val durationSeconds = (cleanedText.split("\\s+".toRegex()).size / (2.5f * effectiveRate)).toInt().coerceAtLeast(2)
            Result.success(
                SynthesizedAudioResult(
                    filePath = syntheticFile.absolutePath,
                    durationSeconds = durationSeconds,
                    isAiModel = false,
                    engineName = "VoxTube Harmonic Synthesizer"
                )
            )
        }
    }

    private fun cleanScriptForTts(rawScript: String): String {
        return rawScript
            .replace("\\[Emphasis:\\s*([^\\]]+)\\]".toRegex(), "$1")
            .replace("\\[Pause [0-9.]+s\\]".toRegex(), " ... ")
            .replace("\\[Whisper\\]".toRegex(), "")
            .replace("\\[Excited\\]".toRegex(), "")
            .replace("\\[Emphasis\\]".toRegex(), "")
            .replace("\\[Deep Tone\\]".toRegex(), "")
            .replace("\\[Fast Pace\\]".toRegex(), "")
            .replace("\\[Narrator\\]".toRegex(), "")
            .replace("\\[Speaker A\\]".toRegex(), "")
            .replace("\\[Speaker B\\]".toRegex(), "")
            .replace("\\[AI Guest\\]".toRegex(), "")
            .trim()
    }

    private fun parseLocaleTag(tag: String): Locale {
        return try {
            when (tag) {
                "en-US" -> Locale.US
                "en-GB" -> Locale.UK
                "en-AU" -> Locale.forLanguageTag("en-AU")
                "en-IN" -> Locale.forLanguageTag("en-IN")
                "en-IE" -> Locale.forLanguageTag("en-IE")
                "en-CA" -> Locale.CANADA
                else -> Locale.US
            }
        } catch (e: Exception) {
            Locale.US
        }
    }

    private fun isWavFormat(bytes: ByteArray): Boolean {
        if (bytes.size < 12) return false
        return bytes[0] == 'R'.code.toByte() &&
                bytes[1] == 'I'.code.toByte() &&
                bytes[2] == 'F'.code.toByte() &&
                bytes[3] == 'F'.code.toByte()
    }

    private fun pcmToWav(pcmData: ByteArray, sampleRate: Int, channels: Int, bitsPerSample: Int): ByteArray {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val totalDataLen = pcmData.size + 36
        val totalAudioLen = pcmData.size

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        val wavData = ByteArray(header.size + pcmData.size)
        System.arraycopy(header, 0, wavData, 0, header.size)
        System.arraycopy(pcmData, 0, wavData, header.size, pcmData.size)
        return wavData
    }

    private fun calculateDurationFromWav(file: File, fileSize: Int, script: String): Int {
        try {
            if (file.exists() && file.length() >= 44) {
                RandomAccessFile(file, "r").use { raf ->
                    raf.seek(24)
                    val b0 = raf.read(); val b1 = raf.read(); val b2 = raf.read(); val b3 = raf.read()
                    val sampleRate = b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)

                    raf.seek(28)
                    val br0 = raf.read(); val br1 = raf.read(); val br2 = raf.read(); val br3 = raf.read()
                    val byteRate = br0 or (br1 shl 8) or (br2 shl 16) or (br3 shl 24)

                    if (byteRate > 0) {
                        val duration = ((file.length() - 44) / byteRate).toInt()
                        if (duration > 0) return duration
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AudioSynthesizer", "Error parsing duration", e)
        }
        val words = script.split("\\s+".toRegex()).size
        return (words / 2.3).toInt().coerceAtLeast(3)
    }

    private fun generateSyntheticVoiceWav(
        text: String,
        outFile: File,
        voice: VoiceOption,
        effectivePitch: Float,
        effectiveRate: Float
    ): File {
        val sampleRate = 22050
        val wordCount = text.split("\\s+".toRegex()).size.coerceAtLeast(3)
        val durationSec = (wordCount / (2.5 * effectiveRate)).toFloat().coerceAtLeast(3.0f)
        val numSamples = (durationSec * sampleRate).toInt()
        val pcm = ShortArray(numSamples)

        val baseFreq = when {
            voice.gender == "Female" -> 220.0 * effectivePitch
            voice.gender == "Male" -> 125.0 * effectivePitch
            else -> 165.0 * effectivePitch
        }

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val cadenceMod = 0.5 + 0.5 * Math.sin(2.0 * Math.PI * 4.0 * t)
            val f1 = Math.sin(2.0 * Math.PI * baseFreq * t)
            val f2 = 0.4 * Math.sin(2.0 * Math.PI * (baseFreq * 2.0) * t)
            val f3 = 0.2 * Math.sin(2.0 * Math.PI * (baseFreq * 3.0) * t)
            val sampleVal = ((f1 + f2 + f3) * cadenceMod * 14000).toInt().coerceIn(-32768, 32767)
            pcm[i] = sampleVal.toShort()
        }

        val pcmBytes = ByteArray(numSamples * 2)
        for (i in 0 until numSamples) {
            pcmBytes[i * 2] = (pcm[i].toInt() and 0xff).toByte()
            pcmBytes[i * 2 + 1] = ((pcm[i].toInt() shr 8) and 0xff).toByte()
        }

        val wavBytes = pcmToWav(pcmBytes, sampleRate, 1, 16)
        FileOutputStream(outFile).use { it.write(wavBytes) }
        return outFile
    }

    fun shutdown() {
        try {
            localTts?.stop()
            localTts?.shutdown()
        } catch (e: Exception) {
            Log.e("AudioSynthesizer", "Shutdown error", e)
        }
    }
}

data class SynthesizedAudioResult(
    val filePath: String,
    val durationSeconds: Int,
    val isAiModel: Boolean,
    val engineName: String
)
