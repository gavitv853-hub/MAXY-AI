package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssistantNote
import com.example.ui.components.SimulatedCallDialog
import com.example.ui.theme.BorderCyanGlow
import com.example.ui.theme.BorderGreenGlow
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MaxyViewModel

@Composable
fun DeviceCommsScreen(
    viewModel: MaxyViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Calling & Messaging, 1: Device Controls, 2: Notes
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val notes by viewModel.assistantNotes.collectAsState()
    val simulatedCallContact by viewModel.simulatedCallContact.collectAsState()

    // Simulated Call Dialog
    simulatedCallContact?.let { contact ->
        SimulatedCallDialog(
            contactName = contact.name,
            phoneNumber = contact.number,
            onSpeak = { text -> viewModel.textToSpeech.speak(text, "hi-IN") },
            onHangUp = { viewModel.dismissSimulatedAiCall() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = "COMMS & DEVICE HUB",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )
        Text(
            text = "Authorized Phone Calling, Smart Messaging, & Android Control",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(Modifier.height(12.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF091224),
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan
                )
            }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("CALLS & SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("DEVICE TOOLS", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("VOICE NOTES", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(Modifier.height(14.dp))

        when (selectedTab) {
            0 -> CallsAndMessagingSection(viewModel)
            1 -> DeviceControlsSection(viewModel, isFlashlightOn)
            2 -> NotesSection(viewModel, notes)
        }
    }
}

@Composable
private fun CallsAndMessagingSection(viewModel: MaxyViewModel) {
    var callQuery by remember { mutableStateOf("Rahul") }
    var smsRecipient by remember { mutableStateOf("Amit") }
    var smsMessage by remember { mutableStateOf("Main 10 minute mein aa raha hoon.") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Calling Assistant Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGreenGlow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = NeonGreen)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("AI CALLING ASSISTANT", fontWeight = FontWeight.Bold, color = NeonGreen)
                            Text("Voice commands: \"MAXY, Rahul ko call karo\"", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = callQuery,
                        onValueChange = { callQuery = it },
                        label = { Text("Contact Name or Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("call_contact_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = Color(0xFF1E3558),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.makeCall(callQuery, direct = false) },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Open Dialer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val c = viewModel.callAssistant.findContact(callQuery)
                                val number = c?.number ?: "+91 98765 43210"
                                viewModel.startSimulatedAiCall(callQuery, number)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF102848), contentColor = NeonCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("AI Call Demo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Smart Messaging Assistant Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCyanGlow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, tint = NeonCyan)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("SMART MESSAGING ASSISTANT", fontWeight = FontWeight.Bold, color = NeonCyan)
                            Text("Voice commands: \"Amit ko message bhejo...\"", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = smsRecipient,
                        onValueChange = { smsRecipient = it },
                        label = { Text("Recipient Name / Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E3558),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = smsMessage,
                        onValueChange = { smsMessage = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth().testTag("sms_body_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E3558),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val c = viewModel.callAssistant.findContact(smsRecipient)
                                val number = c?.number ?: smsRecipient
                                viewModel.sendSms(number, smsMessage, direct = false)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color(0xFF050811)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Send SMS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val c = viewModel.callAssistant.findContact(smsRecipient)
                                val number = c?.number ?: smsRecipient
                                viewModel.sendWhatsApp(number, smsMessage)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceControlsSection(viewModel: MaxyViewModel, isFlashlightOn: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderCyanGlow)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("HARDWARE & QUICK ACTIONS", fontWeight = FontWeight.Bold, color = NeonCyan)
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Flashlight Toggle
                        Button(
                            onClick = { viewModel.toggleFlashlight() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFlashlightOn) NeonAmber else Color(0xFF142442),
                                contentColor = if (isFlashlightOn) Color(0xFF050811) else NeonAmber
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isFlashlightOn) "Torch ON" else "Torch OFF", fontWeight = FontWeight.Bold)
                        }

                        // Set Quick Timer
                        Button(
                            onClick = { viewModel.deviceController.setTimer(300, "MAXY 5 Min Timer") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF142442), contentColor = NeonCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("5m Timer", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF162544))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("OPEN INSTALLED APPS (OFFLINE INTENTS)", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))

                    val apps = listOf(
                        Triple("YouTube", Icons.Default.Videocam, "youtube"),
                        Triple("Camera", Icons.Default.CameraAlt, "camera"),
                        Triple("Settings", Icons.Default.Settings, "settings"),
                        Triple("Chrome", Icons.Default.Language, "chrome"),
                        Triple("Maps", Icons.Default.Map, "maps"),
                        Triple("Contacts", Icons.Default.Call, "contacts")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        apps.take(3).forEach { (name, icon, key) ->
                            Button(
                                onClick = { viewModel.openApp(key) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E38), contentColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            ) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(name, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        apps.drop(3).forEach { (name, icon, key) ->
                            Button(
                                onClick = { viewModel.openApp(key) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F1E38), contentColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            ) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(name, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesSection(viewModel: MaxyViewModel, notes: List<AssistantNote>) {
    var newNoteText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newNoteText,
                onValueChange = { newNoteText = it },
                placeholder = { Text("Quick note or task...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Color(0xFF1E3558),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newNoteText.isNotBlank()) {
                        viewModel.addNote("Voice Note", newNoteText)
                        newNoteText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color(0xFF050811)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (notes.isEmpty()) {
                item {
                    Text("No voice notes created yet. Say: \"MAXY, note likho...\"", color = TextMuted, fontSize = 13.sp)
                }
            }
            items(notes, key = { it.id }) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF162544))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleNoteDone(note) }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Complete",
                                tint = if (note.isCompleted) NeonGreen else TextMuted
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (note.isCompleted) TextMuted else TextPrimary
                            )
                            Text(
                                text = note.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        IconButton(onClick = { viewModel.deleteNote(note) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
