package com.example.notes.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.Database.Note
import com.example.notes.Database.NoteDao
import com.example.notes.Database.NoteDatabase
import com.example.notes.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNoteViewModel(application: Application):AndroidViewModel(application) {

    private val noteRepository: NoteRepository


    init {
        val noteDao: NoteDao = NoteDatabase.getDatabase(application).noteDao()
        noteRepository = NoteRepository(noteDao)
    }

    suspend fun getNote(id:Int):Note?{
            var note:Note?=null
        viewModelScope.launch (Dispatchers.IO){
             note= noteRepository.getNote(id);
        }

        return note;
    }

}