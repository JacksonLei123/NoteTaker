package com.app.notetaker.mvc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(
    private val repository: NoteRepository
): ViewModel() {
    val notes: Flow<List<Note>> = repository.getAllNotes()

    fun createNote(content: String) {
        viewModelScope.launch(Dispatchers.IO){
            repository.insertNote(Note(notes = content))
        }
    }

    fun updateNote(id: Int, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(Note(id, content))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(Note(id, ""))
        }
    }
}

class NoteViewModelFactory(
    private val repository: NoteRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  NoteViewModel(
            repository = repository
        ) as T
    }

}