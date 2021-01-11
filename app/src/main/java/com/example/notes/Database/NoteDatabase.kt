package com.example.notes.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Note::class], version=1,exportSchema = false)
abstract class NoteDatabase :RoomDatabase() {

    abstract fun noteDao():NoteDao

        companion object{

            @Volatile
            private var INSTANCE:NoteDatabase?=null

            fun getDatabase(context:Context):NoteDatabase
            {
                val tempInstance= INSTANCE
                if(tempInstance!=null)
                {
                    return tempInstance
                }
                synchronized(this){
                    val instance = Room.databaseBuilder(context.applicationContext,
                    NoteDatabase::class.java,
                    "note_table").build()
                    INSTANCE=instance
                    return instance
                }
            }


        }
}