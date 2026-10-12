package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class MaxySpeechRecognizer(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _audioRms = MutableStateFlow(0f)
    val audioRms: StateFlow<Boolean> = MutableStateFlow(false) // placeholder to avoid confusion

    private val _rmsLevel = MutableStateFlow(0f) // 0.0f to 1.0f
    val rmsLevel: StateFlow<Float> = _rmsLevel

    private var onSpeechResultListener: ((String) -> Unit)? = null
    private var onErrorListener: ((String) -> Unit)? = null

    fun initialize() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(createListener())
                }
            } catch (e: Exception) {
                speechRecognizer = null
            }
        }
    }

    fun startListening(
        languageCode: String = "hi-IN",
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        this.onSpeechResultListener = onResult
        this.onErrorListener = onError

        if (speechRecognizer == null) {
            initialize()
        }

        val recognizer = speechRecognizer
        if (recognizer == null) {
            onError("Speech recognition not available on this device.")
            _isListening.value = false
            return
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            _isListening.value = true
            _rmsLevel.value = 0.1f
            recognizer.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            onError("Failed to start voice listener: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isListening.value = false
        _rmsLevel.value = 0f
    }

    fun destroy() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        _isListening.value = false
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {
                _isListening.value = true
            }

            override fun onRmsChanged(rmsdB: Float) {
                // rmsdB usually spans -2dB to 10dB
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                _rmsLevel.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
                _rmsLevel.value = 0f
            }

            override fun onError(error: Int) {
                _isListening.value = false
                _rmsLevel.value = 0f
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client recognition error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission required"
                    SpeechRecognizer.ERROR_NETWORK -> "Network required for speech recognition"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please speak again."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "MAXY AI recognizer is busy"
                    SpeechRecognizer.ERROR_SERVER -> "Voice server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech heard"
                    else -> "Speech recognition error ($error)"
                }
                onErrorListener?.invoke(message)
            }

            override fun onResults(results: Bundle?) {
                _isListening.value = false
                _rmsLevel.value = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()?.trim()
                if (!text.isNullOrEmpty()) {
                    onSpeechResultListener?.invoke(text)
                } else {
                    onErrorListener?.invoke("Could not recognize voice instruction.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // Can be used for live transcription if desired
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }
}
