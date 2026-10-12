package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppProjectDao
import com.example.data.dao.ChatDao
import com.example.data.dao.NoteDao
import com.example.data.dao.WebProjectDao
import com.example.data.model.AppProject
import com.example.data.model.AssistantNote
import com.example.data.model.ChatMessage
import com.example.data.model.WebProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessage::class,
        WebProject::class,
        AppProject::class,
        AssistantNote::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MaxyDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun webProjectDao(): WebProjectDao
    abstract fun appProjectDao(): AppProjectDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: MaxyDatabase? = null

        fun getDatabase(context: Context): MaxyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MaxyDatabase::class.java,
                    "maxy_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(database)
                }
            }
        }

        private suspend fun seedDatabase(db: MaxyDatabase) {
            // Seed a starter Gaming Tournament website project requested by user voice
            db.webProjectDao().insertWebProject(
                WebProject(
                    title = "Cyber Gaming Tournament & Arena",
                    prompt = "mere liye ek gaming website banao jisme login aur tournament section ho",
                    htmlContent = DefaultProjects.SAMPLE_WEB_HTML,
                    cssContent = DefaultProjects.SAMPLE_WEB_CSS,
                    jsContent = DefaultProjects.SAMPLE_WEB_JS
                )
            )

            // Seed a starter Android Tournament App project
            db.appProjectDao().insertAppProject(
                AppProject(
                    title = "Apex Esports Tournament App",
                    packageName = "com.maxy.tournament",
                    prompt = "ek tournament app banao jisme login, signup, tournament list aur admin panel ho",
                    filesJson = DefaultProjects.SAMPLE_APP_FILES_JSON
                )
            )

            // Seed a starter note
            db.noteDao().insertNote(
                AssistantNote(
                    title = "Iron Man Mark 85 Upgrades",
                    content = "Review arc reactor pulse frequencies and calibrate voice synthesis models for Hindi & Hinglish."
                )
            )
        }
    }
}
