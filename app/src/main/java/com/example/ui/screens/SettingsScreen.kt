package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderCyanGlow
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MaxyViewModel

@Composable
fun SettingsScreen(
    viewModel: MaxyViewModel,
    modifier: Modifier = Modifier
) {
    val isOnline by viewModel.isOnlineMode.collectAsState()
    val continuousVoice by viewModel.continuousVoice.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var speechRate by remember { mutableFloatStateOf(1.0f) }
    var speechPitch by remember { mutableFloatStateOf(1.0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "JARVIS PROTOCOL SETTINGS",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )
        Text(
            text = "Voice Synthesis, AI Backend & Security Safeguards",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        // 1. Voice Language Selection
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderCyanGlow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("VOICE LANGUAGE ENGINE", fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(Modifier.height(8.dp))
                Text("Select the primary speech recognition and vocal persona:", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(12.dp))

                val languages = listOf(
                    Triple("hi-IN", "हिन्दी (Hindi)", "Natural Hindi voice with Indian phrasing"),
                    Triple("en-IN", "Hinglish / Indian English", "Bilingual blend with Hindi terms"),
                    Triple("en-US", "English (Global)", "Standard English JARVIS persona")
                )

                languages.forEach { (code, name, desc) ->
                    val isSelected = selectedLanguage == code
                    Button(
                        onClick = { viewModel.setLanguage(code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0xFF0F1E38),
                            contentColor = if (isSelected) NeonCyan else TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(desc, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // 2. Continuous Voice Conversation Toggle
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3558))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Continuous Conversation Mode", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Automatically listen for follow-up voice instructions after MAXY finishes speaking.", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = continuousVoice,
                        onCheckedChange = { viewModel.toggleContinuousVoice() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonGreen, checkedTrackColor = Color(0xFF004422))
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Online AI Core (Gemini 3.5)", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("When enabled, connects to Gemini models for deep reasoning and live code generation.", fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { viewModel.toggleOnlineMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF003355))
                    )
                }
            }
        }

        // 3. TTS Voice Tuning (Speed & Pitch)
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3558))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TEXT-TO-SPEECH VOCAL CALIBRATION", fontWeight = FontWeight.Bold, color = NeonCyan)
                Spacer(Modifier.height(10.dp))

                Text("Speech Rate: ${String.format("%.1f", speechRate)}x", fontSize = 12.sp, color = TextSecondary)
                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it
                        viewModel.textToSpeech.setSpeechRate(it)
                    },
                    valueRange = 0.7f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Spacer(Modifier.height(8.dp))

                Text("Vocal Pitch: ${String.format("%.1f", speechPitch)}x", fontSize = 12.sp, color = TextSecondary)
                Slider(
                    value = speechPitch,
                    onValueChange = {
                        speechPitch = it
                        viewModel.textToSpeech.setPitch(it)
                    },
                    valueRange = 0.7f..1.4f,
                    colors = SliderDefaults.colors(thumbColor = NeonGreen, activeTrackColor = NeonGreen)
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        val testPhrase = if (selectedLanguage.startsWith("hi")) {
                            "नमस्ते सर! MAXY AI वॉइस सिंथेसिस पूर्ण रूप से कैलिब्रेट हो चुका है।"
                        } else {
                            "MAXY AI vocal synthesis is fully calibrated and operational, sir."
                        }
                        viewModel.textToSpeech.speak(testPhrase, selectedLanguage)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF112340), contentColor = NeonCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Test AI Voice Synthesizer", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Privacy, Security & Google Play Safeguards
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF080D1A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3558))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("PRIVACY & ETHICAL AI SAFEGUARDS", fontWeight = FontWeight.Bold, color = NeonAmber)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "• AI Call Identification: If the assistant calls another person, it explicitly identifies itself as an AI assistant and requests consent.\n" +
                    "• No Secret Recording: MAXY AI never covertly records telephone calls or captures unauthorized photos.\n" +
                    "• Offline Privacy: Basic commands, device controls, and notes are processed locally on-device.\n" +
                    "• Isolated Workspace: Code projects are kept in secure application storage and exported only when authorized by you.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
