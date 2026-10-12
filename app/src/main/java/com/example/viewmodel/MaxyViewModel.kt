package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.brain.GeminiJarvisEngine
import com.example.brain.OfflineCommandProcessor
import com.example.data.MaxyDatabase
import com.example.data.model.ActionType
import com.example.data.model.AppProject
import com.example.data.model.AssistantNote
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.data.model.WebProject
import com.example.device.DeviceController
import com.example.messaging.MessagingAssistant
import com.example.speech.MaxySpeechRecognizer
import com.example.speech.MaxyTextToSpeech
import com.example.telephony.CallAssistant
import com.example.telephony.ContactInfo
import com.example.ui.components.OrbState
import com.example.appbuilder.AndroidAppGenerator
import com.example.webbuilder.WebsiteGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MaxyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MaxyDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val webDao = db.webProjectDao()
    private val appDao = db.appProjectDao()
    private val noteDao = db.noteDao()

    val chatMessages: StateFlow<List<ChatMessage>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val webProjects: StateFlow<List<WebProject>> = webDao.getAllWebProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val appProjects: StateFlow<List<AppProject>> = appDao.getAllAppProjects()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val assistantNotes: StateFlow<List<AssistantNote>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Subsystems
    val speechRecognizer = MaxySpeechRecognizer(application)
    val textToSpeech = MaxyTextToSpeech(application)
    val offlineBrain = OfflineCommandProcessor()
    val geminiBrain = GeminiJarvisEngine()
    val callAssistant = CallAssistant(application)
    val messagingAssistant = MessagingAssistant(application)
    val deviceController = DeviceController(application)
    val webGenerator = WebsiteGenerator(application)
    val appGenerator = AndroidAppGenerator(application)

    // UI States
    private val _orbState = MutableStateFlow(OrbState.IDLE)
    val orbState: StateFlow<OrbState> = _orbState

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel

    private val _isOnlineMode = MutableStateFlow(true)
    val isOnlineMode: StateFlow<Boolean> = _isOnlineMode

    private val _continuousVoice = MutableStateFlow(false)
    val continuousVoice: StateFlow<Boolean> = _continuousVoice

    private val _selectedLanguage = MutableStateFlow("hi-IN")
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _simulatedCallContact = MutableStateFlow<ContactInfo?>(null)
    val simulatedCallContact: StateFlow<ContactInfo?> = _simulatedCallContact

    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn

    init {
        speechRecognizer.initialize()
        // Collect RMS audio updates
        viewModelScope.launch {
            speechRecognizer.rmsLevel.collect { level ->
                if (_orbState.value == OrbState.LISTENING) {
                    _rmsLevel.value = level
                }
            }
        }

        // Welcome greeting if chat is empty
        viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            val current = chatMessages.value
            if (current.isEmpty()) {
                val welcome = "नमस्ते सर! MAXY AI ऑनलाइन है। मैं कॉल, मैसेज, वेबसाइट और Android ऐप बनाने के लिए तैयार हूँ। आप बोलकर आदेश दे सकते हैं।"
                chatDao.insertMessage(
                    ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = welcome,
                        language = _selectedLanguage.value
                    )
                )
                speakResponse(welcome)
            }
        }
    }

    fun startVoiceListening() {
        if (_orbState.value == OrbState.SPEAKING) {
            textToSpeech.stop()
        }

        _orbState.value = OrbState.LISTENING
        deviceController.vibratePulse()

        speechRecognizer.startListening(
            languageCode = _selectedLanguage.value,
            onResult = { text ->
                _orbState.value = OrbState.PROCESSING
                handleUserInput(text)
            },
            onError = { err ->
                _orbState.value = OrbState.IDLE
            }
        )
    }

    fun stopVoiceListening() {
        speechRecognizer.stopListening()
        _orbState.value = OrbState.IDLE
    }

    fun toggleVoiceListening() {
        if (_orbState.value == OrbState.LISTENING) {
            stopVoiceListening()
        } else {
            startVoiceListening()
        }
    }

    fun handleUserInput(rawInput: String) {
        val input = rawInput.trim()
        if (input.isBlank()) {
            _orbState.value = OrbState.IDLE
            return
        }

        viewModelScope.launch {
            // Save user message
            chatDao.insertMessage(
                ChatMessage(
                    role = MessageRole.USER,
                    text = input,
                    language = _selectedLanguage.value
                )
            )

            _orbState.value = OrbState.PROCESSING

            // Process with Offline Command Processor first
            val offlineResult = offlineBrain.processCommand(input)

            if (offlineResult.isHandledOffline && offlineResult.actionType != ActionType.NONE) {
                // Handle offline action
                executeOfflineAction(offlineResult.actionType, offlineResult.actionPayload)
                
                chatDao.insertMessage(
                    ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = offlineResult.spokenResponse,
                        language = _selectedLanguage.value,
                        actionType = offlineResult.actionType,
                        actionPayload = offlineResult.actionPayload
                    )
                )
                speakResponse(offlineResult.spokenResponse)
                return@launch
            }

            // Web Builder generation
            if (offlineResult.actionType == ActionType.WEB_PROJECT) {
                val project = webGenerator.generateWebsiteFromPrompt(input)
                val id = webDao.insertWebProject(project)
                val msg = "सर, आपकी वेबसाइट '${project.title}' जनरेट कर दी गई है। Web Builder में लाइव प्रीव्यू और ZIP डाउनलोड उपलब्ध है।"
                chatDao.insertMessage(
                    ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = msg,
                        actionType = ActionType.WEB_PROJECT,
                        actionPayload = id.toString()
                    )
                )
                speakResponse(msg)
                return@launch
            }

            // App Builder generation
            if (offlineResult.actionType == ActionType.APP_PROJECT) {
                val appProj = appGenerator.generateAppFromPrompt(input)
                val id = appDao.insertAppProject(appProj)
                val msg = "सर, आपका नया Android Studio प्रोजेक्ट '${appProj.title}' तैयार कर दिया गया है। App Builder टैब में सभी Kotlin फ़ाइलें और Gradle कॉन्फ़िगरेशन देख सकते हैं।"
                chatDao.insertMessage(
                    ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = msg,
                        actionType = ActionType.APP_PROJECT,
                        actionPayload = id.toString()
                    )
                )
                speakResponse(msg)
                return@launch
            }

            // Online Gemini JARVIS Core query
            val history = chatMessages.value.takeLast(6).map {
                (if (it.role == MessageRole.USER) "user" else "model") to it.text
            }

            val reply = if (_isOnlineMode.value) {
                geminiBrain.queryJarvis(input, history)
            } else {
                offlineBrain.processCommand(input).spokenResponse.ifBlank {
                    "सर, MAXY AI अभी ऑफलाइन मोड में है। आप कॉल, मैसेज, टॉर्च या ऐप खोलने के आदेश दे सकते हैं।"
                }
            }

            chatDao.insertMessage(
                ChatMessage(
                    role = MessageRole.ASSISTANT,
                    text = reply,
                    language = _selectedLanguage.value
                )
            )
            speakResponse(reply)
        }
    }

    private fun executeOfflineAction(type: ActionType, payload: String?) {
        when (type) {
            ActionType.DEVICE_CONTROL -> {
                if (payload == "FLASHLIGHT_ON") {
                    _isFlashlightOn.value = deviceController.toggleFlashlight(true)
                } else if (payload == "FLASHLIGHT_OFF") {
                    _isFlashlightOn.value = deviceController.toggleFlashlight(false)
                } else if (payload?.startsWith("OPEN_APP:") == true) {
                    val appKey = payload.removePrefix("OPEN_APP:")
                    deviceController.openApplication(appKey)
                }
            }
            ActionType.NOTE_CREATED -> {
                if (!payload.isNullOrBlank()) {
                    viewModelScope.launch {
                        noteDao.insertNote(
                            AssistantNote(
                                title = "Voice Note (${java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())})",
                                content = payload
                            )
                        )
                    }
                }
            }
            else -> {}
        }
    }

    private fun speakResponse(text: String) {
        _orbState.value = OrbState.SPEAKING
        textToSpeech.speak(text, _selectedLanguage.value) {
            _orbState.value = OrbState.IDLE
            if (_continuousVoice.value) {
                startVoiceListening()
            }
        }
    }

    fun makeCall(contactName: String, direct: Boolean = false) {
        val contact = callAssistant.findContact(contactName)
        if (contact != null) {
            if (direct) {
                callAssistant.makeDirectCallIfPermitted(contact.number)
            } else {
                callAssistant.launchDialer(contact.number)
            }
        } else {
            callAssistant.launchDialer("+91")
        }
    }

    fun startSimulatedAiCall(contactName: String, number: String = "+91 98765 43210") {
        _simulatedCallContact.value = ContactInfo(contactName, number)
    }

    fun dismissSimulatedAiCall() {
        textToSpeech.stop()
        _simulatedCallContact.value = null
        _orbState.value = OrbState.IDLE
    }

    fun sendSms(phoneNumber: String, message: String, direct: Boolean = false) {
        if (direct) {
            messagingAssistant.sendDirectSmsIfPermitted(phoneNumber, message)
        } else {
            messagingAssistant.sendViaSmsApp(phoneNumber, message)
        }
    }

    fun sendWhatsApp(phoneNumber: String?, message: String) {
        messagingAssistant.sendViaWhatsApp(phoneNumber, message)
    }

    fun toggleFlashlight() {
        _isFlashlightOn.value = deviceController.toggleFlashlight()
    }

    fun openApp(appKey: String) {
        deviceController.openApplication(appKey)
    }

    fun exportWebProject(project: WebProject) {
        val file = webGenerator.exportToZip(project)
        if (file != null) {
            webGenerator.shareZipFile(file)
        }
    }

    fun exportAppProject(project: AppProject) {
        val file = appGenerator.exportToZip(project)
        if (file != null) {
            appGenerator.shareZipFile(file)
        }
    }

    fun saveNewWebProject(title: String, prompt: String, html: String, css: String, js: String) {
        viewModelScope.launch {
            webDao.insertWebProject(
                WebProject(title = title, prompt = prompt, htmlContent = html, cssContent = css, jsContent = js)
            )
        }
    }

    fun updateWebProject(project: WebProject) {
        viewModelScope.launch {
            webDao.updateWebProject(project)
        }
    }

    fun toggleNoteDone(note: AssistantNote) {
        viewModelScope.launch {
            noteDao.updateNote(note.copy(isCompleted = !note.isCompleted))
        }
    }

    fun deleteNote(note: AssistantNote) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            noteDao.insertNote(AssistantNote(title = title, content = content))
        }
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun toggleContinuousVoice() {
        _continuousVoice.value = !_continuousVoice.value
    }

    fun toggleOnlineMode() {
        _isOnlineMode.value = !_isOnlineMode.value
    }

    fun clearChat() {
        viewModelScope.launch {
            chatDao.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        textToSpeech.destroy()
    }
}
