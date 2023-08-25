package com.rifaqat.notesmaker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_table")
data class NotesEntity(
    @PrimaryKey(autoGenerate = true) val key: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "color_code") val colorCode: Int = -1
) : Serializable
/*We use serializable if we want to pass the objects/data of that NotesEntity class from one fragment to another fragment.For more check
nav_graph.xml. Previously this work was done by intent by passing data through put_extra and receiving
through get_string extra.
 */