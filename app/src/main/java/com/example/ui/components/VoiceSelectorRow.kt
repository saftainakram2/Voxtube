package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmotionalToneOption
import com.example.data.model.VoiceOption
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan

@Composable
fun VoiceSelectorSection(
    voices: List<VoiceOption>,
    selectedVoice: VoiceOption,
    onVoiceSelected: (VoiceOption) -> Unit,
    emotionalTones: List<EmotionalToneOption>,
    selectedTone: EmotionalToneOption,
    onToneSelected: (EmotionalToneOption) -> Unit,
    onCreateCloneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedGenderFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "YouTube Pro", "Documentary", "Gaming/Hype", "Explainer", "Storytelling", "Cloned")
    val genderFilters = listOf("All Genders", "Male", "Female", "Neutral")

    val filteredVoices = remember(voices, selectedCategory, selectedGenderFilter) {
        voices.filter { voice ->
            val matchCat = (selectedCategory == "All" || voice.category == selectedCategory)
            val matchGender = when (selectedGenderFilter) {
                "Male" -> voice.gender == "Male"
                "Female" -> voice.gender == "Female"
                "Neutral" -> voice.gender == "Neutral" || voice.gender == "Custom"
                else -> true
            }
            matchCat && matchGender
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Voice Models & Accents",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            TextButton(onClick = onCreateCloneClick) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeonCoral)
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Clone Voice", color = NeonCoral, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricPurple,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Gender & Age Filters
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            items(genderFilters) { g ->
                val isSelected = (g == selectedGenderFilter) || (g == "All Genders" && selectedGenderFilter == "All")
                SuggestionChip(
                    onClick = { selectedGenderFilter = if (g == "All Genders") "All" else g },
                    label = { Text(g, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0xFF1E1730),
                        labelColor = if (isSelected) NeonCyan else Color(0xFF94A3B8)
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = if (isSelected) NeonCyan else Color(0xFF2C2444)
                    )
                )
            }
        }

        // Voice Cards Carousel
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredVoices, key = { it.id }) { voice ->
                VoiceOptionCard(
                    voice = voice,
                    isSelected = voice.id == selectedVoice.id,
                    onClick = { onVoiceSelected(voice) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Emotional Tone Selector Sub-Section
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Emotional Tone & Inflection",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = selectedTone.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 4.dp)
            ) {
                items(emotionalTones, key = { it.id }) { tone ->
                    val isSelected = tone.id == selectedTone.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF2E2414) else Color(0xFF161224),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) GoldAccent else Color(0xFF2D2444)
                        ),
                        modifier = Modifier.clickable { onToneSelected(tone) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tone.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GoldAccent else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceOptionCard(
    voice: VoiceOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) NeonCyan else Color(0xFF2E2744)
    val bgColor = if (isSelected) Color(0xFF1F1836) else Color(0xFF141024)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
            .width(186.dp)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(voice.timbreColorHex).copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (voice.isClone) Icons.Default.Mic else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(voice.timbreColorHex),
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(NeonCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } else if (voice.isClone) {
                    Surface(
                        color = NeonCoral.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "CLONE",
                            color = NeonCoral,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0xFF221A38),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = voice.gender,
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = voice.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    color = Color(0xFF251E38),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = voice.accent,
                        fontSize = 10.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = voice.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
        }
    }
}
