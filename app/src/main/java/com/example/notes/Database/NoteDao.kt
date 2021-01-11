package com.example.notes.Database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(note: Note)
        @Update
       suspend fun update(note: Note)
        @Delete
        suspend fun delete(note: Note)
        @Query("SELECT  * FROM note_table ORDER  BY priority DESC")
        fun getAllNotes() :LiveData<List<Note>>
        @Query("DELETE  FROM note_table ")
        suspend fun deleteAllNotes()

        @Query("SELECT * FROM note_table where title LIKE:searchQuery")
        fun searchDatabase(searchQuery:String):LiveData<List<Note>>

}