package com.app.notetaker.mvc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.app.notetaker.openai.ChatResponse
import com.app.notetaker.openai.chatCompletion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    val noteId: Int
): ViewModel() {
    private val _aiSummaryApiState = MutableStateFlow<UiState<ChatResponse>>(UiState.Idle)
    val aiSummaryApiState: StateFlow<UiState<ChatResponse>> = _aiSummaryApiState

    fun requestAISummary(scanText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _aiSummaryApiState.value = UiState.Loading
            try {
                chatCompletion(scanText).collect {
                    _aiSummaryApiState.value = UiState.Success(it)
                }
            } catch (e: Exception) {
                _aiSummaryApiState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
    fun resetApiState() {
        _aiSummaryApiState.value = UiState.Idle
    }
}

class NoteDetailViewModelFactory(
    private val noteId: Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  NoteDetailViewModel(
            noteId = noteId
        ) as T
    }

}
