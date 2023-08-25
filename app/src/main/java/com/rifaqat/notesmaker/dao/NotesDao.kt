package com.rifaqat.notesmaker.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rifaqat.notesmaker.models.NotesEntity

@Dao
interface NotesDao {

    /*Sometimes there is a problem when data is insert if same key is passed to new data which already
    exist so we write a parameter in @Insert to ignore it.*/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun create(notes: NotesEntity)

    @Query("SELECT *FROM note_table ORDER BY `key` DESC")
    fun read(): LiveData<List<NotesEntity>>

    @Update
    suspend fun update(notes: NotesEntity)

    @Delete
    suspend fun delete(notes: NotesEntity)


    @Query("SELECT *FROM note_table WHERE title LIKE:query OR description LIKE:query OR date LIKE:query ORDER BY `key` DESC")
    fun search(query:String): LiveData<List<NotesEntity>>
}