package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.model.ActionType
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.ui.components.AppProjectCard
import com.example.ui.components.HolographicOrb
import com.example.ui.components.MessagingActionCard
import com.example.ui.components.OrbState
import com.example.ui.components.PhoneCallActionCard
import com.example.ui.components.SimulatedCallDialog
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.components.WebProjectCard
import com.example.ui.theme.BorderCyanGlow
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
fun HomeScreen(
    viewModel: MaxyViewModel,
    onNavigateToWeb: (Long?) -> Unit,
    onNavigateToApp: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val orbState by viewModel.orbState.collectAsState()
    val rmsLevel by viewModel.rmsLevel.collectAsState()
    val isOnline by viewModel.isOnlineMode.collectAsState()
    val continuousVoice by viewModel.continuousVoice.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val simulatedCallContact by viewModel.simulatedCallContact.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Simulated Call Dialog
    simulatedCallContact?.let { contact ->
        SimulatedCallDialog(
            contactName = contact.name,
            phoneNumber = contact.number,
            onSpeak = { text -> viewModel.textToSpeech.speak(text, selectedLanguage) },
            onHangUp = { viewModel.dismissSimulatedAiCall() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // Top HUD Header
        HudTopBar(
            isOnline = isOnline,
            language = selectedLanguage,
            continuousVoice = continuousVoice,
            onToggleContinuous = { viewModel.toggleContinuousVoice() },
            onClearChat = { viewModel.clearChat() }
        )

        // Holographic Arc Reactor Core & Audio Visualizer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HolographicOrb(
                state = orbState,
                rmsLevel = rmsLevel,
                size = 140.dp,
                onClick = { viewModel.toggleVoiceListening() }
            )

            Spacer(Modifier.height(6.dp))

            val statusText = when (orbState) {
                OrbState.LISTENING -> "LISTENING... (बोलिए सर)"
                OrbState.PROCESSING -> "PROCESSING QUERY..."
                OrbState.SPEAKING -> "MAXY AI SPEAKING..."
                OrbState.ALERT -> "SYSTEM NOTICE"
                OrbState.IDLE -> "JARVIS CORE ONLINE • TAP ORB TO SPEAK"
            }
            Text(
                text = statusText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = when (orbState) {
                    OrbState.LISTENING -> NeonGreen
                    OrbState.PROCESSING -> NeonAmber
                    OrbState.SPEAKING -> NeonCyan
                    else -> TextSecondary
                }
            )

            Spacer(Modifier.height(6.dp))

            VoiceWaveformVisualizer(
                isActive = orbState == OrbState.LISTENING || orbState == OrbState.SPEAKING,
                rmsLevel = rmsLevel,
                height = 20.dp,
                barCount = 20,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        // Quick Voice Command Chips
        QuickVoiceChips(onChipClick = { command -> viewModel.handleUserInput(command) })

        // Conversation History Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubbleItem(
                    message = msg,
                    viewModel = viewModel,
                    onOpenWeb = { id -> onNavigateToWeb(id) },
                    onOpenApp = { id -> onNavigateToApp(id) }
                )
            }
        }

        // Bottom Input Controls Bar
        BottomInputBar(
            textInput = textInput,
            onTextChange = { textInput = it },
            isListening = orbState == OrbState.LISTENING,
            onSend = {
                if (textInput.isNotBlank()) {
                    val prompt = textInput
                    textInput = ""
                    viewModel.handleUserInput(prompt)
                }
            },
            onMicClick = { viewModel.toggleVoiceListening() }
        )
    }
}

@Composable
private fun HudTopBar(
    isOnline: Boolean,
    language: String,
    continuousVoice: Boolean,
    onToggleContinuous: () -> Unit,
    onClearChat: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isOnline) NeonGreen else NeonAmber)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = if (isOnline) "ONLINE • GEMINI" else "OFFLINE • LOCAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOnline) NeonGreen else NeonAmber
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Language badge
            Box(
                modifier = Modifier
                    .background(Color(0xFF0F1E3A), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (language.startsWith("hi")) "HINDI / HINGLISH" else "ENGLISH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }

            Spacer(Modifier.width(8.dp))

            // Continuous mode pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onToggleContinuous() }
                    .background(if (continuousVoice) NeonGreen.copy(alpha = 0.2f) else Color(0xFF0F1E3A))
                    .border(1.dp, if (continuousVoice) NeonGreen else Color.Transparent, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (continuousVoice) "LOOP VOICE ON" else "LOOP OFF",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (continuousVoice) NeonGreen else TextSecondary
                )
            }

            IconButton(onClick = onClearChat, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Clear, contentDescription = "Clear Chat", tint = TextMuted, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun QuickVoiceChips(onChipClick: (String) -> Unit) {
    val chips = listOf(
        "📞 राहुल को कॉल करो",
        "💬 अमित को SMS भेजो",
        "🌐 Gaming website banao",
        "📱 Tournament app banao",
        "🔦 Torch chalu karo",
        "⏱️ Time kya hua hai?",
        "📝 Important note likho"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips) { chip ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0C162E))
                    .border(1.dp, BorderCyanGlow, RoundedCornerShape(16.dp))
                    .clickable { onChipClick(chip.substring(3).trim()) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = chip, fontSize = 12.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun MessageBubbleItem(
    message: ChatMessage,
    viewModel: MaxyViewModel,
    onOpenWeb: (Long?) -> Unit,
    onOpenApp: (Long?) -> Unit
) {
    val isUser = message.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 2.dp)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "MAXY AI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = NeonCyan
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(if (isUser) 14.dp else 4.dp))
                .background(if (isUser) Color(0xFF0D2545) else CyberSurfaceCard)
                .border(
                    width = 1.dp,
                    color = if (isUser) BorderCyanGlow else Color(0x301E3558),
                    shape = RoundedCornerShape(if (isUser) 14.dp else 4.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }

        // Action Cards attached to message
        when (message.actionType) {
            ActionType.PHONE_CALL -> {
                val contactName = message.actionPayload ?: "Friend"
                val contact = viewModel.callAssistant.findContact(contactName)
                val number = contact?.number ?: "+91 98765 43210"
                Spacer(Modifier.height(6.dp))
                PhoneCallActionCard(
                    contactName = contactName,
                    phoneNumber = number,
                    onDialerClick = { viewModel.makeCall(contactName, direct = false) },
                    onDirectCallClick = { viewModel.makeCall(contactName, direct = true) },
                    onSimulateAiCall = { viewModel.startSimulatedAiCall(contactName, number) }
                )
            }
            ActionType.SMS_MESSAGE, ActionType.WHATSAPP_MESSAGE -> {
                val parts = message.actionPayload?.split("|||") ?: listOf("Friend", "Hello")
                val recipient = parts.getOrNull(0) ?: "Friend"
                val msg = parts.getOrNull(1) ?: "Hello"
                val contact = viewModel.callAssistant.findContact(recipient)
                val number = contact?.number ?: "+91 98765 43210"
                Spacer(Modifier.height(6.dp))
                MessagingActionCard(
                    recipientName = recipient,
                    messageText = msg,
                    onSendSms = { viewModel.sendSms(number, msg, direct = false) },
                    onSendWhatsApp = { viewModel.sendWhatsApp(number, msg) }
                )
            }
            ActionType.WEB_PROJECT -> {
                val projId = message.actionPayload?.toLongOrNull()
                Spacer(Modifier.height(6.dp))
                WebProjectCard(
                    title = "Generated Website Project",
                    onOpenPreview = { onOpenWeb(projId) },
                    onExportZip = {
                        val proj = viewModel.webProjects.value.firstOrNull { it.id == projId }
                            ?: viewModel.webProjects.value.firstOrNull()
                        proj?.let { viewModel.exportWebProject(it) }
                    }
                )
            }
            ActionType.APP_PROJECT -> {
                val appProjId = message.actionPayload?.toLongOrNull()
                Spacer(Modifier.height(6.dp))
                AppProjectCard(
                    title = "Generated Android Studio Project",
                    packageName = "com.maxy.generated",
                    onOpenProject = { onOpenApp(appProjId) },
                    onExportZip = {
                        val appProj = viewModel.appProjects.value.firstOrNull { it.id == appProjId }
                            ?: viewModel.appProjects.value.firstOrNull()
                        appProj?.let { viewModel.exportAppProject(it) }
                    }
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun BottomInputBar(
    textInput: String,
    onTextChange: (String) -> Unit,
    isListening: Boolean,
    onSend: () -> Unit,
    onMicClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF080D1D))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textInput,
            onValueChange = onTextChange,
            placeholder = { Text("Ask MAXY or speak...", fontSize = 13.sp, color = TextMuted) },
            modifier = Modifier
                .weight(1f)
                .testTag("chat_text_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color(0xFF1E3355),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            trailingIcon = {
                if (textInput.isNotBlank()) {
                    IconButton(onClick = onSend, modifier = Modifier.testTag("send_button")) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = NeonCyan)
                    }
                }
            }
        )

        Spacer(Modifier.width(10.dp))

        FloatingActionButton(
            onClick = onMicClick,
            modifier = Modifier
                .size(52.dp)
                .testTag("mic_fab_button"),
            containerColor = if (isListening) NeonCrimson else NeonCyan,
            contentColor = Color(0xFF050811),
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = "Voice Input",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
