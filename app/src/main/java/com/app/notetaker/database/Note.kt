package com.app.notetaker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Note(
    @ColumnInfo(name = "Notes") val notes: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

}