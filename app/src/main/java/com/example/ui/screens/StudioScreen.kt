package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VoicePresets
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: StudioViewModel,
    onNavigateToClones: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val projectTitle by viewModel.projectTitle.collectAsState()
    val currentScript by viewModel.currentScript.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()
    val allVoices by viewModel.allVoices.collectAsState()
    val pitchMultiplier by viewModel.pitchMultiplier.collectAsState()
    val speedMultiplier by viewModel.speedMultiplier.collectAsState()
    val wordCount by viewModel.wordCount.collectAsState()
    val characterCount by viewModel.characterCount.collectAsState()
    val estimatedDuration by viewModel.estimatedDurationFormatted.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationMessage by viewModel.generationMessage.collectAsState()
    val selectedBgMusic by viewModel.selectedBgMusic.collectAsState()
    val bgMusicVolume by viewModel.bgMusicVolume.collectAsState()
    val activeAudioResult by viewModel.activeAudioResult.collectAsState()

    var showCloneDialog by remember { mutableStateOf(false) }
    var showAiToolsDialog by remember { mutableStateOf(false) }
    var showMixerControls by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VoxTube",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 19.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = NeonCoral,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "STUDIO",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "AI Voice, Accents & YouTube Narration",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAiToolsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Tools",
                            tint = GoldAccent
                        )
                    }
                    IconButton(onClick = onNavigateToTemplates) {
                        Icon(
                            imageVector = Icons.Default.LibraryBooks,
                            contentDescription = "Templates",
                            tint = NeonCyan
                        )
                    }
                    IconButton(onClick = onNavigateToProjects) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Saved Projects",
                            tint = ElectricPurpleLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioDarkBg)
            )
        },
        containerColor = StudioDarkBg,
        bottomBar = {
            if (activeAudioResult != null) {
                FloatingAudioPlayerBar(
                    audioPlayer = viewModel.audioPlayer,
                    title = projectTitle,
                    voiceName = "${selectedVoice.name} • ${selectedTone.name}",
                    onExportClick = {
                        val activeProject = viewModel.savedProjects.value.firstOrNull {
                            it.audioFilePath == activeAudioResult?.filePath
                        }
                        if (activeProject != null) {
                            viewModel.shareProjectAudio(context, activeProject)
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Project Title Bar
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161224)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.dp, Color(0xFF2B2344), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = NeonCoral,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = projectTitle,
                            onValueChange = { viewModel.updateProjectTitle(it) },
                            placeholder = { Text("Enter Video Project Title...", color = Color(0xFF64748B)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Script Editor Box
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141022)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.5.dp, Color(0xFF28213E), RoundedCornerShape(18.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Editor Toolbar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NARRATION SCRIPT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(
                                    onClick = { showAiToolsDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("AI Hooks", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = { viewModel.polishScriptPacing() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Fix Pacing", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { viewModel.updateScript("") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Clear", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = currentScript,
                            onValueChange = { viewModel.updateScript(it) },
                            placeholder = {
                                Text(
                                    "Paste or write your YouTube video script here...\n\nTip: Use [Emphasis: your word] to highlight key terms!",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp, max = 280.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFF1F5F9),
                                lineHeight = 22.sp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // YouTube Pacing and Metrics Gauge
                        YouTubePacingMeter(
                            wordCount = wordCount,
                            characterCount = characterCount,
                            estimatedDuration = estimatedDuration,
                            speechRateMultiplier = speedMultiplier
                        )
                    }
                }
            }

            // Voice Selector Carousel & Emotional Tones
            item {
                Spacer(modifier = Modifier.height(10.dp))
                VoiceSelectorSection(
                    voices = allVoices,
                    selectedVoice = selectedVoice,
                    onVoiceSelected = { viewModel.selectVoice(it) },
                    emotionalTones = viewModel.allEmotionalTones,
                    selectedTone = selectedTone,
                    onToneSelected = { viewModel.selectEmotionalTone(it) },
                    onCreateCloneClick = { showCloneDialog = true }
                )
            }

            // Studio Mixer & Pitch/Speed Modifiers (Collapsible)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141022)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .border(1.dp, Color(0xFF28213E), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMixerControls = !showMixerControls },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = ElectricPurpleLight, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Vocal Pitch, Pacing & Background Music",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = if (showMixerControls) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                        AnimatedVisibility(visible = showMixerControls) {
                            Column(modifier = Modifier.padding(top = 14.dp)) {
                                // Pitch Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Voice Pitch Modulation", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "${(pitchMultiplier * 100).toInt()}% (${if (pitchMultiplier < 1.0f) "Deeper Bass" else if (pitchMultiplier > 1.0f) "Higher Treble" else "Default"})",
                                        fontSize = 12.sp,
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Slider(
                                    value = pitchMultiplier,
                                    onValueChange = { viewModel.setPitch(it) },
                                    valueRange = 0.75f..1.35f,
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Speed / Pacing Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Speech Rate (Pacing)", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "${(speedMultiplier * 100).toInt()}% (~${(140 * speedMultiplier * selectedTone.rateShift).toInt()} WPM)",
                                        fontSize = 12.sp,
                                        color = ElectricPurpleLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Slider(
                                    value = speedMultiplier,
                                    onValueChange = { viewModel.setSpeed(it) },
                                    valueRange = 0.75f..1.45f,
                                    colors = SliderDefaults.colors(thumbColor = ElectricPurple, activeTrackColor = ElectricPurple)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Background Music Selector
                                Text(
                                    text = "Background Ambience Music",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                var bgMenuOpen by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedButton(
                                        onClick = { bgMenuOpen = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            val currentTrack = VoicePresets.BACKGROUND_TRACKS.firstOrNull { it.id == selectedBgMusic }
                                            Text(currentTrack?.name ?: "None", color = Color.White)
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = bgMenuOpen,
                                        onDismissRequest = { bgMenuOpen = false }
                                    ) {
                                        VoicePresets.BACKGROUND_TRACKS.forEach { track ->
                                            DropdownMenuItem(
                                                text = { Text("${track.name} (${track.genre})") },
                                                onClick = {
                                                    viewModel.setBackgroundMusic(track.id, bgMusicVolume)
                                                    bgMenuOpen = false
                                                }
                                            )
                                        }
                                    }
                                }

                                if (selectedBgMusic != "none") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Music Ducking Volume", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text("$bgMusicVolume%", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                    }
                                    Slider(
                                        value = bgMusicVolume.toFloat(),
                                        onValueChange = { viewModel.setBackgroundMusic(selectedBgMusic, it.toInt()) },
                                        valueRange = 5f..50f,
                                        colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Big Synthesize Voiceover Action Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { viewModel.generateAudio() },
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(ElectricPurple, NeonCoral)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = generationMessage.ifBlank { "Synthesizing Audio..." },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generate Studio Voiceover",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCloneDialog) {
        VoiceCloneRecorderDialog(
            viewModel = viewModel,
            onDismiss = { showCloneDialog = false },
            onCloneCreated = {
                showCloneDialog = false
            }
        )
    }

    if (showAiToolsDialog) {
        ScriptAiToolsDialog(
            viewModel = viewModel,
            onDismiss = { showAiToolsDialog = false }
        )
    }
}
