package com.app.notetaker.mvc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.notetaker.database.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NoteViewModel(
    private val db: NoteDao
): ViewModel() {
    val notes: Flow<List<Note>> = db.getAll()

    fun createNote(content: String) {
        viewModelScope.launch(Dispatchers.IO){
            db.insertNote(Note(notes = content))
        }
    }

    fun updateNote(id: Int, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateNote(Note(id, content))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteNote(Note(id, ""))
        }
    }



}

class NoteViewModelFactory(
    private val db: NoteDao,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  NoteViewModel(
            db = db,
        ) as T
    }

}