package com.example.notes.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.notes.Database.Note
import com.example.notes.Database.NoteDao
import com.example.notes.Database.NoteDatabase
import com.example.notes.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val allnotes: LiveData<List<Note>>

    private val noteRepository: NoteRepository


    init {
        val noteDao: NoteDao = NoteDatabase.getDatabase(application).noteDao()
        noteRepository = NoteRepository(noteDao)
        allnotes = noteRepository.getAll()
    }


    // DATABASE QUERY FUNCTIONS

    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.addNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

    fun deleteAllNote() {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteAll()
        }
    }

    fun getAllNote(): LiveData<List<Note>> {
        return noteRepository.getAll()
    }

    fun searchDatabase(query: String): LiveData<List<Note>> {
        return noteRepository.searchDatabase(query)
    }


}