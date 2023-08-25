package com.rifaqat.notesmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.rifaqat.notesmaker.database.NotesDatabase
import com.rifaqat.notesmaker.databinding.ActivityMainBinding
import com.rifaqat.notesmaker.repository.NotesRepo
import com.rifaqat.notesmaker.viewmodels.NotesViewModel
import com.rifaqat.notesmaker.viewmodels.NotesViewModelFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: NotesViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)

        try {
            val repo=NotesRepo(NotesDatabase.getDatabase(this))
            viewModel= ViewModelProvider(this,NotesViewModelFactory(repo))[NotesViewModel::class.java]
        }
        catch (e:Exception){
            Log.d("TAG",e.message.toString())
        }
    }
}