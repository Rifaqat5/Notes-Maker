package com.rifaqat.notesmaker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rifaqat.notesmaker.dao.NotesDao
import com.rifaqat.notesmaker.models.NotesEntity

@Database(entities = [NotesEntity::class], exportSchema = false, version = 2)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun getNotesDao(): NotesDao

    companion object {
        @Volatile
        private var instance: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext, NotesDatabase::class.java, "notes_database"
                )
                    .fallbackToDestructiveMigration() // Handles schema changes by recreating the database
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}
