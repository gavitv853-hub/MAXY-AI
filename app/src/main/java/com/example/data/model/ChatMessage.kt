package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

enum class ActionType {
    NONE,
    PHONE_CALL,
    SMS_MESSAGE,
    WHATSAPP_MESSAGE,
    WEB_PROJECT,
    APP_PROJECT,
    DEVICE_CONTROL,
    NOTE_CREATED,
    BUILD_GUIDE
}

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: MessageRole,
    val text: String,
    val language: String = "hi-IN", // "hi-IN", "en-IN", "en-US"
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: ActionType = ActionType.NONE,
    val actionPayload: String? = null // JSON or formatted payload (recipient, command, projectId, etc.)
)
