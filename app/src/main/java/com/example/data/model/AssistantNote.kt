package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistant_notes")
data class AssistantNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
