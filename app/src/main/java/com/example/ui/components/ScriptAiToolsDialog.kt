package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScriptAiToolsDialog(
    viewModel: StudioViewModel,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Hooks, 1: Script Gen, 2: Pacing & Emphasis
    val isEnhancing by viewModel.isEnhancingScript.collectAsState()
    val viralHooks by viewModel.viralHooks.collectAsState()

    var topicInput by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf(2) }
    var selectedTone by remember { mutableStateOf("High-Energy & Punchy") }
    var emphasisWordInput by remember { mutableStateOf("") }

    val tones = listOf("High-Energy & Punchy", "Cinematic & Mysterious", "Educational & Clear", "Casual Tech Review", "Motivational")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().padding(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF141022),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "YouTube AI Script Engine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E1834),
                    contentColor = NeonCyan
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Viral Hooks", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Script Gen", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Pacing & Emphasis", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEnhancing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NeonCyan)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gemini AI is crafting your YouTube content...",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                } else {
                    when (selectedTab) {
                        0 -> {
                            // Viral Hooks Tab
                            Text(
                                text = "Generate 3 high-retention opening hooks for the first 3-5 seconds of your video.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.generateViralHooks() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Viral Hooks", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (viralHooks.isNotEmpty()) {
                                viralHooks.forEachIndexed { idx, hook ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1732)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .border(1.dp, Color(0xFF332A4E), RoundedCornerShape(12.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Hook #${idx + 1}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GoldAccent
                                                )

                                                TextButton(
                                                    onClick = {
                                                        viewModel.applyHookToScript(hook)
                                                        onDismiss()
                                                    }
                                                ) {
                                                    Text("Use Hook", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Text(
                                                text = "\"$hook\"",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Script Generator Tab
                            OutlinedTextField(
                                value = topicInput,
                                onValueChange = { topicInput = it },
                                label = { Text("Video Title or Topic") },
                                placeholder = { Text("e.g. 5 AI Tools That Will Replace Programmers in 2026") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = Color(0xFF332D48)
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Target Duration: $durationMinutes min (~${durationMinutes * 140} words)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            Slider(
                                value = durationMinutes.toFloat(),
                                onValueChange = { durationMinutes = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = NeonCyan
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Narration Tone",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            var toneMenuOpen by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { toneMenuOpen = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(selectedTone, color = Color.White)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                    }
                                }

                                DropdownMenu(
                                    expanded = toneMenuOpen,
                                    onDismissRequest = { toneMenuOpen = false }
                                ) {
                                    tones.forEach { t ->
                                        DropdownMenuItem(
                                            text = { Text(t) },
                                            onClick = {
                                                selectedTone = t
                                                toneMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (topicInput.isNotBlank()) {
                                        viewModel.generateScriptFromTopic(topicInput, durationMinutes, selectedTone)
                                        onDismiss()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                                modifier = Modifier.fillMaxWidth().height(46.dp)
                            ) {
                                Text("Generate Full YouTube Script", fontWeight = FontWeight.Bold)
                            }
                        }

                        2 -> {
                            // Pacing & Emphasis Tab
                            Text(
                                text = "Add natural speech pauses and vocal emphasis on key words or phrases for higher retention.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Word Emphasis Builder
                            OutlinedTextField(
                                value = emphasisWordInput,
                                onValueChange = { emphasisWordInput = it },
                                label = { Text("Word or Phrase to Emphasize") },
                                placeholder = { Text("e.g. game-changing, critical step") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCoral,
                                    unfocusedBorderColor = Color(0xFF332D48)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (emphasisWordInput.isNotBlank()) {
                                        viewModel.wrapSelectionWithEmphasis(emphasisWordInput.trim())
                                        emphasisWordInput = ""
                                        onDismiss()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCoral),
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Icon(Icons.Default.FormatBold, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Apply Vocal Emphasis to Word", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.polishScriptPacing()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Timer, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Auto-Polish Speech Pacing (AI)", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Quick Insert Tags:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val quickTags = listOf(
                                "[Pause 0.5s]", "[Pause 1.0s]", "[Whisper]",
                                "[Excited]", "[Deep Tone]", "[Emphasis]",
                                "[Fast Pace]", "[Narrator]", "[Speaker A]"
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                quickTags.forEach { tag ->
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.insertPacingTag(tag)
                                        },
                                        label = { Text(tag, fontSize = 11.sp) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = Color(0xFF221C34),
                                            labelColor = NeonCyan
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
