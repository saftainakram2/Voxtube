package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiScriptAssistant
import com.example.data.audio.AudioExporter
import com.example.data.audio.AudioPlayerManager
import com.example.data.audio.AudioSynthesizer
import com.example.data.audio.SynthesizedAudioResult
import com.example.data.audio.VoiceCloneAnalyzer
import com.example.data.local.AppDatabase
import com.example.data.local.VoicePresets
import com.example.data.model.AudioProject
import com.example.data.model.EmotionalToneOption
import com.example.data.model.VideoChapterMarker
import com.example.data.model.VoiceCloneProfile
import com.example.data.model.VoiceOption
import com.example.data.model.YouTubeScriptTemplate
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val projectDao = db.projectDao()
    private val cloneDao = db.voiceCloneDao()

    val audioPlayer = AudioPlayerManager(application)
    val synthesizer = AudioSynthesizer(application)
    val cloneAnalyzer = VoiceCloneAnalyzer(application)
    val scriptAssistant = GeminiScriptAssistant()

    // Room Flows
    val savedProjects: StateFlow<List<AudioProject>> = projectDao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userClones: StateFlow<List<VoiceCloneProfile>> = cloneDao.getAllClones()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Studio State
    private val _projectTitle = MutableStateFlow("New YouTube Narration")
    val projectTitle: StateFlow<String> = _projectTitle.asStateFlow()

    private val _currentScript = MutableStateFlow(VoicePresets.YOUTUBE_TEMPLATES.first().sampleScript)
    val currentScript: StateFlow<String> = _currentScript.asStateFlow()

    private val _selectedVoice = MutableStateFlow<VoiceOption>(VoicePresets.DEFAULT_VOICES.first())
    val selectedVoice: StateFlow<VoiceOption> = _selectedVoice.asStateFlow()

    val allEmotionalTones: List<EmotionalToneOption> = VoicePresets.EMOTIONAL_TONES
    private val _selectedTone = MutableStateFlow<EmotionalToneOption>(VoicePresets.EMOTIONAL_TONES.first())
    val selectedTone: StateFlow<EmotionalToneOption> = _selectedTone.asStateFlow()

    private val _pitchMultiplier = MutableStateFlow(1.0f)
    val pitchMultiplier: StateFlow<Float> = _pitchMultiplier.asStateFlow()

    private val _speedMultiplier = MutableStateFlow(1.0f)
    val speedMultiplier: StateFlow<Float> = _speedMultiplier.asStateFlow()

    private val _selectedBgMusic = MutableStateFlow("none")
    val selectedBgMusic: StateFlow<String> = _selectedBgMusic.asStateFlow()

    private val _bgMusicVolume = MutableStateFlow(25)
    val bgMusicVolume: StateFlow<Int> = _bgMusicVolume.asStateFlow()

    // Generation State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationMessage = MutableStateFlow("")
    val generationMessage: StateFlow<String> = _generationMessage.asStateFlow()

    private val _activeAudioResult = MutableStateFlow<SynthesizedAudioResult?>(null)
    val activeAudioResult: StateFlow<SynthesizedAudioResult?> = _activeAudioResult.asStateFlow()

    private val _lastSavedProjectId = MutableStateFlow<Long?>(null)
    val lastSavedProjectId: StateFlow<Long?> = _lastSavedProjectId.asStateFlow()

    // Script AI & Hooks
    private val _viralHooks = MutableStateFlow<List<String>>(emptyList())
    val viralHooks: StateFlow<List<String>> = _viralHooks.asStateFlow()

    private val _isEnhancingScript = MutableStateFlow(false)
    val isEnhancingScript: StateFlow<Boolean> = _isEnhancingScript.asStateFlow()

    // Voice Clone Creation State
    private val _isRecordingClone = MutableStateFlow(false)
    val isRecordingClone: StateFlow<Boolean> = _isRecordingClone.asStateFlow()

    private val _cloneRecordingFile = MutableStateFlow<File?>(null)

    // Snackbar notifications
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Combined available voices (Presets + User Clones)
    val allVoices: StateFlow<List<VoiceOption>> = combine(_selectedVoice, userClones) { _, clones ->
        val cloneVoiceOptions = clones.map { clone ->
            VoiceOption(
                id = "clone_${clone.id}",
                name = "${clone.cloneName} (Clone)",
                gender = "Custom",
                ageBracket = clone.ageBracket,
                accent = clone.accent,
                category = "Cloned",
                description = clone.description,
                geminiVoiceName = clone.baseVoiceModel,
                localeTag = "en-US",
                defaultPitch = clone.pitchMultiplier,
                defaultRate = clone.speedMultiplier,
                recommendedTone = "High-Energy & Engaging",
                isClone = true,
                clonePersonaPrompt = clone.clonePromptInstructions,
                timbreColorHex = 0xFFFF3366
            )
        }
        VoicePresets.DEFAULT_VOICES + cloneVoiceOptions
    }.stateIn(viewModelScope, SharingStarted.Lazily, VoicePresets.DEFAULT_VOICES)

    // Derived Metrics
    val wordCount: StateFlow<Int> = _currentScript.map { script ->
        script.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val characterCount: StateFlow<Int> = _currentScript.map { it.length }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val estimatedDurationFormatted: StateFlow<String> = combine(wordCount, _speedMultiplier, _selectedTone) { words, speed, tone ->
        val effectiveWpm = (140 * speed * tone.rateShift).coerceAtLeast(60f)
        val totalSec = ((words / effectiveWpm) * 60).toInt()
        val min = totalSec / 60
        val sec = totalSec % 60
        String.format("%02d:%02d", min, sec)
    }.stateIn(viewModelScope, SharingStarted.Lazily, "00:00")

    fun updateScript(newScript: String) {
        _currentScript.value = newScript
    }

    fun updateProjectTitle(newTitle: String) {
        _projectTitle.value = newTitle
    }

    fun selectVoice(voice: VoiceOption) {
        _selectedVoice.value = voice
        _pitchMultiplier.value = voice.defaultPitch
        _speedMultiplier.value = voice.defaultRate
        audioPlayer.setSpeed(voice.defaultRate)

        // Switch to matching recommended tone if present
        val matchingTone = allEmotionalTones.firstOrNull { it.name == voice.recommendedTone }
        if (matchingTone != null) {
            _selectedTone.value = matchingTone
        }
    }

    fun selectEmotionalTone(tone: EmotionalToneOption) {
        _selectedTone.value = tone
    }

    fun setPitch(pitch: Float) {
        _pitchMultiplier.value = pitch
    }

    fun setSpeed(speed: Float) {
        _speedMultiplier.value = speed
        audioPlayer.setSpeed(speed * _selectedTone.value.rateShift)
    }

    fun setBackgroundMusic(trackId: String, volume: Int) {
        _selectedBgMusic.value = trackId
        _bgMusicVolume.value = volume
        audioPlayer.setBackgroundMusic(trackId, volume)
    }

    fun insertPacingTag(tag: String) {
        val current = _currentScript.value
        _currentScript.value = if (current.isBlank()) tag else "$current\n$tag\n"
    }

    fun wrapSelectionWithEmphasis(selectedText: String) {
        if (selectedText.isBlank()) {
            insertPacingTag("[Emphasis]")
            return
        }
        val current = _currentScript.value
        if (current.contains(selectedText)) {
            _currentScript.value = current.replaceFirst(selectedText, "[Emphasis: $selectedText]")
            _snackbarMessage.value = "Emphasized: \"$selectedText\""
        }
    }

    fun loadTemplate(template: YouTubeScriptTemplate) {
        _projectTitle.value = template.title
        _currentScript.value = template.sampleScript
        val suggestedVoice = allVoices.value.firstOrNull { it.id == template.suggestedVoiceId }
            ?: VoicePresets.DEFAULT_VOICES.first()
        selectVoice(suggestedVoice)
        val tone = allEmotionalTones.firstOrNull { it.name == template.suggestedTone }
            ?: VoicePresets.EMOTIONAL_TONES.first()
        _selectedTone.value = tone
        _snackbarMessage.value = "Loaded \"${template.title}\" template!"
    }

    fun loadExistingProject(project: AudioProject) {
        _projectTitle.value = project.title
        _currentScript.value = project.scriptText
        val voice = allVoices.value.firstOrNull { it.id == project.voiceId }
            ?: VoicePresets.DEFAULT_VOICES.first()
        _selectedVoice.value = voice

        val tone = allEmotionalTones.firstOrNull { it.name == project.emotionalTone }
            ?: VoicePresets.EMOTIONAL_TONES.first()
        _selectedTone.value = tone

        _selectedBgMusic.value = project.backgroundMusicTrack
        _bgMusicVolume.value = project.musicVolumePercent
        audioPlayer.setBackgroundMusic(project.backgroundMusicTrack, project.musicVolumePercent)

        if (File(project.audioFilePath).exists()) {
            audioPlayer.loadAndPlay(project.audioFilePath, autoPlay = true)
            _activeAudioResult.value = SynthesizedAudioResult(
                filePath = project.audioFilePath,
                durationSeconds = project.durationSeconds,
                isAiModel = true,
                engineName = project.voiceName
            )
        }
        _lastSavedProjectId.value = project.id
        _snackbarMessage.value = "Loaded project: \"${project.title}\""
    }

    fun generateAudio() {
        val script = _currentScript.value.trim()
        if (script.isBlank()) {
            _snackbarMessage.value = "Please enter or paste a script to generate audio."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _generationMessage.value = "Preparing neural voice synthesis..."

            val voice = _selectedVoice.value
            val tone = _selectedTone.value
            val result = synthesizer.synthesizeSpeech(
                script = script,
                voice = voice,
                emotionalTone = tone,
                pitchMultiplier = _pitchMultiplier.value,
                speedMultiplier = _speedMultiplier.value,
                onProgressUpdate = { msg -> _generationMessage.value = msg }
            )

            result.onSuccess { audioResult ->
                _activeAudioResult.value = audioResult
                _isGenerating.value = false
                _generationMessage.value = ""

                // Automatically load and start playback
                audioPlayer.loadAndPlay(audioResult.filePath, autoPlay = true)
                audioPlayer.setBackgroundMusic(_selectedBgMusic.value, _bgMusicVolume.value)

                // Save to Room DB project history
                saveCurrentProject(audioResult)
                _snackbarMessage.value = "Voiceover ready! Playing audio."
            }.onFailure { error ->
                _isGenerating.value = false
                _generationMessage.value = ""
                _snackbarMessage.value = "Synthesis error: ${error.message ?: "Unknown error"}"
            }
        }
    }

    private fun saveCurrentProject(audioResult: SynthesizedAudioResult) {
        viewModelScope.launch {
            val title = _projectTitle.value.ifBlank { "YouTube Voiceover" }
            val chapterMarkers = AudioExporter.generateYouTubeTimestamps(_currentScript.value, audioResult.durationSeconds)
            val chaptersJson = AudioExporter.formatChaptersForYouTube(chapterMarkers)

            val project = AudioProject(
                id = _lastSavedProjectId.value ?: 0,
                title = title,
                scriptText = _currentScript.value,
                voiceId = _selectedVoice.value.id,
                voiceName = _selectedVoice.value.name,
                voiceAccent = _selectedVoice.value.accent,
                emotionalTone = _selectedTone.value.name,
                audioFilePath = audioResult.filePath,
                durationSeconds = audioResult.durationSeconds,
                wordCount = wordCount.value,
                estimatedVideoDuration = estimatedDurationFormatted.value,
                backgroundMusicTrack = _selectedBgMusic.value,
                musicVolumePercent = _bgMusicVolume.value,
                chaptersJson = chaptersJson
            )

            val newId = projectDao.insertProject(project)
            _lastSavedProjectId.value = newId
        }
    }

    fun generateViralHooks() {
        val text = _currentScript.value.ifBlank { _projectTitle.value }
        if (text.isBlank()) {
            _snackbarMessage.value = "Enter a script or title first to generate hooks."
            return
        }

        viewModelScope.launch {
            _isEnhancingScript.value = true
            val result = scriptAssistant.generateViralHooks(text)
            result.onSuccess { hooks ->
                _viralHooks.value = hooks
            }
            _isEnhancingScript.value = false
        }
    }

    fun applyHookToScript(hook: String) {
        val current = _currentScript.value
        _currentScript.value = "$hook\n\n$current"
        _snackbarMessage.value = "Applied hook to beginning of script!"
    }

    fun polishScriptPacing() {
        val text = _currentScript.value
        if (text.isBlank()) return

        viewModelScope.launch {
            _isEnhancingScript.value = true
            val result = scriptAssistant.polishRhythmAndPacing(text)
            result.onSuccess { polished ->
                _currentScript.value = polished
                _snackbarMessage.value = "Rhythm & pacing polished with speech pauses!"
            }
            _isEnhancingScript.value = false
        }
    }

    fun generateScriptFromTopic(topic: String, durationMinutes: Int, tone: String) {
        viewModelScope.launch {
            _isEnhancingScript.value = true
            _projectTitle.value = topic
            val result = scriptAssistant.generateFullYouTubeScript(topic, durationMinutes, tone)
            result.onSuccess { script ->
                _currentScript.value = script
                _snackbarMessage.value = "Generated full YouTube script for \"$topic\"!"
            }
            _isEnhancingScript.value = false
        }
    }

    fun startVoiceCloneRecording() {
        viewModelScope.launch {
            val audioDir = File(getApplication<Application>().filesDir, "audio_clones").apply { mkdirs() }
            val outputFile = File(audioDir, "clone_ref_${System.currentTimeMillis()}.wav")
            _cloneRecordingFile.value = outputFile
            _isRecordingClone.value = true

            cloneAnalyzer.startRecording(outputFile)
        }
    }

    fun stopVoiceCloneRecording() {
        cloneAnalyzer.stopRecording()
        _isRecordingClone.value = false
    }

    fun finalizeVoiceClone(
        cloneName: String,
        accent: String,
        style: String,
        onSuccess: (VoiceCloneProfile) -> Unit
    ) {
        val wavFile = _cloneRecordingFile.value
        if (wavFile == null || !wavFile.exists()) {
            _snackbarMessage.value = "No recorded reference audio found."
            return
        }

        viewModelScope.launch {
            val result = cloneAnalyzer.analyzeAndCreateCloneProfile(
                cloneName = cloneName.ifBlank { "My Voice Clone" },
                wavFile = wavFile,
                accentSelection = accent,
                targetStyle = style
            )

            result.onSuccess { profile ->
                val id = cloneDao.insertClone(profile)
                val savedProfile = profile.copy(id = id)
                _snackbarMessage.value = "Voice Clone \"${profile.cloneName}\" created!"
                onSuccess(savedProfile)
            }.onFailure { error ->
                _snackbarMessage.value = "Clone creation failed: ${error.message}"
            }
        }
    }

    fun deleteProject(project: AudioProject) {
        viewModelScope.launch {
            projectDao.deleteProject(project)
            File(project.audioFilePath).delete()
            if (_lastSavedProjectId.value == project.id) {
                _lastSavedProjectId.value = null
            }
            _snackbarMessage.value = "Project deleted."
        }
    }

    fun deleteClone(clone: VoiceCloneProfile) {
        viewModelScope.launch {
            cloneDao.deleteClone(clone)
            File(clone.referenceAudioPath).delete()
            _snackbarMessage.value = "Voice Clone removed."
        }
    }

    fun toggleFavorite(project: AudioProject) {
        viewModelScope.launch {
            projectDao.toggleFavorite(project.id, !project.isFavorite)
        }
    }

    fun shareProjectAudio(context: Context, project: AudioProject) {
        val success = AudioExporter.shareAudioFile(context, project)
        if (!success) {
            _snackbarMessage.value = "Audio file not found on device."
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        synthesizer.shutdown()
    }
}
