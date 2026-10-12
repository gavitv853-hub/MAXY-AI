package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AssistantNote
import com.example.data.model.ChatMessage
import com.example.data.model.WebProject
import com.example.data.model.AppProject
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface WebProjectDao {
    @Query("SELECT * FROM web_projects ORDER BY updatedAt DESC")
    fun getAllWebProjects(): Flow<List<WebProject>>

    @Query("SELECT * FROM web_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): WebProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebProject(project: WebProject): Long

    @Update
    suspend fun updateWebProject(project: WebProject)

    @Delete
    suspend fun deleteWebProject(project: WebProject)
}

@Dao
interface AppProjectDao {
    @Query("SELECT * FROM app_projects ORDER BY updatedAt DESC")
    fun getAllAppProjects(): Flow<List<AppProject>>

    @Query("SELECT * FROM app_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): AppProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppProject(project: AppProject): Long

    @Update
    suspend fun updateAppProject(project: AppProject)

    @Delete
    suspend fun deleteAppProject(project: AppProject)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM assistant_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<AssistantNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: AssistantNote): Long

    @Update
    suspend fun updateNote(note: AssistantNote)

    @Delete
    suspend fun deleteNote(note: AssistantNote)
}
