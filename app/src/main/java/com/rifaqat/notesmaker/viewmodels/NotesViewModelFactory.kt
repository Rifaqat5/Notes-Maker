package com.rifaqat.notesmaker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rifaqat.notesmaker.repository.NotesRepo

class NotesViewModelFactory(private val repo: NotesRepo):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(repo) as T
    }
}