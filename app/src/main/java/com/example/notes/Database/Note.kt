package com.example.notes.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note (
                    @PrimaryKey(autoGenerate = true)
                    @ColumnInfo(name = "note_id")
                     val id:Int,
                     var title:String?,
                     var description:String?,
                      val priority: Int,
                    val imagePath:String?
)