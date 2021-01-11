package com.example.notes.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note (
                    @PrimaryKey(autoGenerate = true)
                     val id:Int,
                     var title:String?,
                     var description:String?,
                      val priority: Int
)