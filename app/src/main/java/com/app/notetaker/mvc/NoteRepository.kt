package com.app.notetaker.mvc

import com.app.notetaker.database.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAll()
    }

    fun getNote(noteId: Int): Flow<Note> {
        return noteDao.getNote(noteId)
    }

    suspend fun updateNote(note: Note) {
        return noteDao.updateNote(note)
    }

    suspend fun insertNote(note: Note) {
        return noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        return noteDao.deleteNote(note)
    }
}
