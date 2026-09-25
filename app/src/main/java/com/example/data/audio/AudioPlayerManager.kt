package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileInputStream
import kotlin.math.sin

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var bgMusicPlayer: MediaPlayer? = null
    private var bgTrackAudio: AudioTrack? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _waveformAmplitudes = MutableStateFlow<List<Float>>(emptyList())
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    private val _activeFilePath = MutableStateFlow<String?>(null)
    val activeFilePath: StateFlow<String?> = _activeFilePath.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null
    private var bgSynthJob: Job? = null

    private var activeBgMusicTrack: String = "none"
    private var bgMusicVolumePercent: Int = 20

    fun loadAndPlay(filePath: String, autoPlay: Boolean = true) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.e("AudioPlayerManager", "File not found: $filePath")
            return
        }

        stop()
        _activeFilePath.value = filePath

        // Extract waveform data in background
        scope.launch(Dispatchers.IO) {
            val amplitudes = extractWaveform(filePath)
            _waveformAmplitudes.value = amplitudes
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                _durationMs.value = duration
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        playbackParams = PlaybackParams().apply { speed = _playbackSpeed.value }
                    } catch (e: Exception) {
                        Log.e("AudioPlayerManager", "PlaybackParams error", e)
                    }
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPositionMs.value = 0
                    stopProgressTracking()
                    stopBgMusic()
                }
            }

            if (autoPlay) {
                play()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error preparing MediaPlayer", e)
        }
    }

    fun play() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                player.start()
                _isPlaying.value = true
                startProgressTracking()
                startBgMusicIfConfigured()
            }
        }
    }

    fun pause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                _isPlaying.value = false
                stopProgressTracking()
                pauseBgMusic()
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { player ->
            val target = positionMs.coerceIn(0, player.duration)
            player.seekTo(target)
            _currentPositionMs.value = target
        }
    }

    fun seekToFraction(fraction: Float) {
        val dur = _durationMs.value
        if (dur > 0) {
            val targetMs = (dur * fraction.coerceIn(0f, 1f)).toInt()
            seekTo(targetMs)
        }
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let { player ->
                    val wasPlaying = player.isPlaying
                    player.playbackParams = player.playbackParams.apply { this.speed = speed }
                    if (!wasPlaying) {
                        player.pause()
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioPlayerManager", "Error setting speed", e)
            }
        }
    }

    fun setBackgroundMusic(trackId: String, volumePercent: Int) {
        activeBgMusicTrack = trackId
        bgMusicVolumePercent = volumePercent
        if (_isPlaying.value) {
            startBgMusicIfConfigured()
        }
    }

    private fun startBgMusicIfConfigured() {
        if (activeBgMusicTrack == "none" || bgMusicVolumePercent <= 0) {
            stopBgMusic()
            return
        }

        // Generate synthetic ambient chord progression in real-time
        stopBgMusic()
        bgSynthJob = scope.launch(Dispatchers.Default) {
            runAmbientSynthesizer(activeBgMusicTrack, bgMusicVolumePercent / 100f)
        }
    }

    private fun pauseBgMusic() {
        stopBgMusic()
    }

    private fun stopBgMusic() {
        bgSynthJob?.cancel()
        bgSynthJob = null
        try {
            bgTrackAudio?.stop()
            bgTrackAudio?.release()
            bgTrackAudio = null
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun CoroutineScope.runAmbientSynthesizer(trackId: String, volume: Float) {
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        bgTrackAudio = audioTrack
        audioTrack.play()

        val chordFreqs = when (trackId) {
            "lofi_focus" -> listOf(130.81, 164.81, 196.00, 246.94) // Cmaj7 warm
            "tech_modern" -> listOf(146.83, 174.61, 220.00, 261.63) // Dm7 synth
            "cinematic_deep" -> listOf(87.31, 110.00, 130.81, 164.81) // F low cinematic
            "acoustic_uplift" -> listOf(196.00, 246.94, 293.66, 392.00) // G bright
            "epic_trailer" -> listOf(110.00, 138.59, 164.81, 220.00) // A dramatic
            else -> listOf(130.81, 164.81, 196.00)
        }

        val frameChunk = 1024
        val buffer = ShortArray(frameChunk)
        var sampleIndex = 0L

        try {
            while (isActive) {
                val chordIndex = ((sampleIndex / (sampleRate * 3)) % chordFreqs.size).toInt()
                val currentFreq = chordFreqs[chordIndex]

                for (i in 0 until frameChunk) {
                    val t = (sampleIndex + i).toDouble() / sampleRate
                    // Ambient pad with slow LFO filter
                    val lfo = 0.5 + 0.5 * sin(2.0 * Math.PI * 0.25 * t)
                    val wave1 = sin(2.0 * Math.PI * currentFreq * t)
                    val wave2 = 0.4 * sin(2.0 * Math.PI * (currentFreq * 1.5) * t)
                    val wave3 = 0.2 * sin(2.0 * Math.PI * (currentFreq * 2.0) * t)
                    val sample = ((wave1 + wave2 + wave3) * lfo * volume * 0.35 * 16000).toInt()
                    buffer[i] = sample.coerceIn(-32768, 32767).toShort()
                }

                audioTrack.write(buffer, 0, frameChunk)
                sampleIndex += frameChunk
            }
        } catch (e: Exception) {
            // Cancelled
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = scope.launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition
                        _durationMs.value = player.duration
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun extractWaveform(filePath: String): List<Float> {
        return try {
            val file = File(filePath)
            val bytes = FileInputStream(file).use { it.readBytes() }
            if (bytes.size < 44) return generatePlaceholderWaveform(40)

            val pcmOffset = 44
            val sampleCount = (bytes.size - pcmOffset) / 2
            if (sampleCount <= 0) return generatePlaceholderWaveform(40)

            val targetBars = 48
            val samplesPerBar = (sampleCount / targetBars).coerceAtLeast(1)
            val bars = mutableListOf<Float>()

            for (b in 0 until targetBars) {
                var maxAmp = 0f
                val start = pcmOffset + (b * samplesPerBar * 2)
                val end = (start + samplesPerBar * 2).coerceAtMost(bytes.size - 1)

                for (s in start until end step 4) {
                    if (s + 1 < bytes.size) {
                        val sample = (bytes[s].toInt() and 0xFF) or (bytes[s + 1].toInt() shl 8)
                        val shortVal = sample.toShort()
                        val normalized = Math.abs(shortVal.toFloat()) / 32768f
                        if (normalized > maxAmp) maxAmp = normalized
                    }
                }
                bars.add((maxAmp * 1.4f).coerceIn(0.12f, 1.0f))
            }
            bars
        } catch (e: Exception) {
            generatePlaceholderWaveform(48)
        }
    }

    private fun generatePlaceholderWaveform(count: Int): List<Float> {
        val list = mutableListOf<Float>()
        for (i in 0 until count) {
            val wave = (0.25f + 0.65f * Math.sin(i * 0.35).toFloat().let { Math.abs(it) }).coerceIn(0.15f, 1.0f)
            list.add(wave)
        }
        return list
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error stopping player", e)
        }
        stopProgressTracking()
        stopBgMusic()
        _isPlaying.value = false
        _currentPositionMs.value = 0
    }

    fun release() {
        stop()
        scope.cancel()
    }
}
