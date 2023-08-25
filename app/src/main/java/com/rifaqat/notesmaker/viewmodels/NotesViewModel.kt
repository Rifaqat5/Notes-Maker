package com.rifaqat.notesmaker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifaqat.notesmaker.models.NotesEntity
import com.rifaqat.notesmaker.repository.NotesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(private val repo:NotesRepo):ViewModel() {

    fun create(notes: NotesEntity)=viewModelScope.launch(Dispatchers.IO) {
        repo.create(notes)
    }

    fun read():LiveData<List<NotesEntity>>{
        return repo.read()
    }

    fun update(notes: NotesEntity)=viewModelScope.launch(Dispatchers.IO) {
        repo.update(notes)
    }

    fun delete(notes: NotesEntity)=viewModelScope.launch(Dispatchers.IO) {
        repo.delete(notes)
    }

    fun search(query:String):LiveData<List<NotesEntity>>{
        return repo.search(query)
    }

}