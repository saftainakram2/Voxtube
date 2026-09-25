package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.VoiceCloneProfile
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCloneRecorderDialog(
    viewModel: StudioViewModel,
    onDismiss: () -> Unit,
    onCloneCreated: (VoiceCloneProfile) -> Unit
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

    var cloneName by remember { mutableStateOf("My YouTube Voice") }
    var selectedAccent by remember { mutableStateOf("US Modern YouTube") }
    var selectedTone by remember { mutableStateOf("High-Energy Creator") }
    var step by remember { mutableStateOf(1) } // 1: Setup, 2: Record, 3: Analyzing / Done

    val isRecording by viewModel.isRecordingClone.collectAsState()
    val amplitude by viewModel.cloneAnalyzer.recordingAmplitude.collectAsState()
    val recordSeconds by viewModel.cloneAnalyzer.recordingSeconds.collectAsState()
    val analysisProgress by viewModel.cloneAnalyzer.analysisProgress.collectAsState()

    val accents = listOf(
        "US Modern YouTube",
        "British RP Classic",
        "Australian Creator",
        "Indian Tech English",
        "Irish Storyteller",
        "Canadian Neutral",
        "Deep Cinematic Bass"
    )

    val tones = listOf(
        "High-Energy Creator",
        "Deep Documentary",
        "Crisp Tech Reviewer",
        "Storytelling & Fiction",
        "Relaxed / Explainer"
    )

    // Pulse animation when recording
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    AlertDialog(
        onDismissRequest = {
            if (isRecording) viewModel.stopVoiceCloneRecording()
            onDismiss()
        },
        modifier = Modifier.fillMaxWidth().padding(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF141022),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NeonCoral.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = null,
                                tint = NeonCoral,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Voice Clone Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            if (isRecording) viewModel.stopVoiceCloneRecording()
                            onDismiss()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (step == 1) {
                    // Step 1: Configuration
                    OutlinedTextField(
                        value = cloneName,
                        onValueChange = { cloneName = it },
                        label = { Text("Voice Clone Name") },
                        placeholder = { Text("e.g. My Studio Voiceover") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF332D48)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Target Accent",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    var accentMenuOpen by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { accentMenuOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedAccent)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = accentMenuOpen,
                            onDismissRequest = { accentMenuOpen = false }
                        ) {
                            accents.forEach { acc ->
                                DropdownMenuItem(
                                    text = { Text(acc) },
                                    onClick = {
                                        selectedAccent = acc
                                        accentMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Delivery Tone & Style",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    var toneMenuOpen by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { toneMenuOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedTone)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = toneMenuOpen,
                            onDismissRequest = { toneMenuOpen = false }
                        ) {
                            tones.forEach { tn ->
                                DropdownMenuItem(
                                    text = { Text(tn) },
                                    onClick = {
                                        selectedTone = tn
                                        toneMenuOpen = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (!hasMicPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                step = 2
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Next: Record Voice Sample", fontWeight = FontWeight.Bold)
                    }

                } else if (step == 2) {
                    // Step 2: Voice Sample Recording
                    Text(
                        text = "Read the sample prompt below aloud clearly into your microphone for 10-15 seconds:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFF1B152E),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"Welcome back to the channel! Today we are exploring the most powerful creative workflows that will transform your video production in 2026.\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF1F5F9),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(14.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Pulse Mic Button
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRecording) NeonCoral.copy(alpha = 0.3f)
                                else ElectricPurple.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isRecording) (70 * pulseScale).dp else 70.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isRecording) NeonCoral else ElectricPurple
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {
                                    if (isRecording) {
                                        viewModel.stopVoiceCloneRecording()
                                    } else {
                                        viewModel.startVoiceCloneRecording()
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = if (isRecording) "Stop" else "Record",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isRecording) "Recording... ${recordSeconds}s / 10s minimum"
                        else if (recordSeconds >= 5) "Sample recorded (${recordSeconds}s)! Ready to build clone."
                        else "Tap to start recording",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isRecording) NeonCoral else if (recordSeconds >= 5) Color(0xFF34D399) else Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (recordSeconds >= 5 && !isRecording) {
                        Button(
                            onClick = {
                                step = 3
                                viewModel.finalizeVoiceClone(
                                    cloneName = cloneName,
                                    accent = selectedAccent,
                                    style = selectedTone,
                                    onSuccess = { created ->
                                        onCloneCreated(created)
                                        onDismiss()
                                    }
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Build AI Voice Clone", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (step == 3) {
                    // Step 3: Analysis in progress
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = analysisProgress ?: "Synthesizing vocal resonance...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
