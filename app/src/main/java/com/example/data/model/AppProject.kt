package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_projects")
data class AppProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val packageName: String,
    val prompt: String,
    // JSON-serialized or delimiter-encoded Map of relative filePath -> code content
    val filesJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
