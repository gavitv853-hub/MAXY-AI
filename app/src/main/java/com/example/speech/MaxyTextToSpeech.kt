package com.example.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class MaxyTextToSpeech(
    private val context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private var speechRate: Float = 1.0f
    private var speechPitch: Float = 1.0f
    private var onSpeechDoneCallback: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                configureDefaults()
                onInitComplete(true)
            } else {
                isInitialized = false
                onInitComplete(false)
            }
        }
    }

    private fun configureDefaults() {
        tts?.apply {
            setSpeechRate(speechRate)
            setPitch(speechPitch)
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeechDoneCallback?.invoke()
                    onSpeechDoneCallback = null
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    onSpeechDoneCallback = null
                }
            })
        }
    }

    fun speak(
        text: String,
        preferredLanguage: String = "hi-IN",
        onDone: (() -> Unit)? = null
    ) {
        if (!isInitialized || tts == null) {
            onDone?.invoke()
            return
        }

        this.onSpeechDoneCallback = onDone

        // Detect language from text content
        val locale = detectLocale(text, preferredLanguage)
        tts?.language = locale
        tts?.setSpeechRate(speechRate)
        tts?.setPitch(speechPitch)

        // Clean markdown symbols for cleaner voice read-out
        val cleanSpeechText = cleanTextForSpeech(text)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "maxy_voice_${System.currentTimeMillis()}")
        }

        tts?.speak(cleanSpeechText, TextToSpeech.QUEUE_FLUSH, params, "maxy_voice_${System.currentTimeMillis()}")
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
        onSpeechDoneCallback = null
    }

    fun setSpeechRate(rate: Float) {
        this.speechRate = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(this.speechRate)
    }

    fun setPitch(pitch: Float) {
        this.speechPitch = pitch.coerceIn(0.5f, 2.0f)
        tts?.setPitch(this.speechPitch)
    }

    fun destroy() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
    }

    private fun detectLocale(text: String, preferred: String): Locale {
        val hasDevanagari = text.any { it in '\u0900'..'\u097F' }
        if (hasDevanagari || preferred.startsWith("hi")) {
            return Locale("hi", "IN")
        }
        return Locale.ENGLISH
    }

    private fun cleanTextForSpeech(input: String): String {
        return input
            .replace(Regex("```[\\s\\S]*?```"), "Code snippet generated.")
            .replace(Regex("`.*?`"), "")
            .replace(Regex("[#*_~]"), "")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            .trim()
    }
}
