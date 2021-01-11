package com.example.notes

import androidx.lifecycle.LiveData
import com.example.notes.Database.Note
import com.example.notes.Database.NoteDao

class NoteRepository (private val noteDao: NoteDao){

     val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    public fun getAll():LiveData<List<Note>>
    {
        return noteDao.getAllNotes()
    }
    fun searchDatabase(query:String):LiveData<List<Note>>
    {
        return noteDao.searchDatabase(query)
    }

    public suspend fun addNote(note:Note)
    {
        noteDao.insert(note)
    }
    public suspend fun deleteNote(note:Note)
    {
        noteDao.delete(note)
    }
    public suspend fun updateNote(note:Note)
    {
        noteDao.update(note)
    }
    public suspend fun deleteAll()
    {
        noteDao.deleteAllNotes()
    }
}