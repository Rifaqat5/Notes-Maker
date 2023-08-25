package com.rifaqat.notesmaker.repository

import com.rifaqat.notesmaker.database.NotesDatabase
import com.rifaqat.notesmaker.models.NotesEntity

class NotesRepo(private val database: NotesDatabase) {

    suspend fun create(notes: NotesEntity)=database.getNotesDao().create(notes)

    fun read()=database.getNotesDao().read()

    suspend fun update(notes: NotesEntity)=database.getNotesDao().update(notes)

    suspend fun delete(notes: NotesEntity)=database.getNotesDao().delete(notes)

    fun search(query:String)=database.getNotesDao().search(query)
}