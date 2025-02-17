package com.app.notetaker.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.notetaker.mvc.Note
import com.app.notetaker.mvc.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    notesState: State<List<Note>>,
    noteViewModel: NoteViewModel,
    param: String
) {
    val note = if (param.isNotEmpty()) notesState.value.find {it.uid == param.toInt()}?.notes else ""
    val state = remember { TextFieldState(note!!)}
    fun navigateBack() {
        if (param.isNotEmpty()) {
            noteViewModel.updateNote(param.toInt(), state.text.toString())
        } else {
            noteViewModel.createNote(state.text.toString())
        }
        navController.navigate(Screen.MainScreen.route)

    }

    @Composable
    fun detailsComponent() {
        val focusManager = LocalFocusManager.current
        var isFocused by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    },
                    title = {},
                    actions = {
                        if (isFocused) {
                            IconButton(onClick = { focusManager.clearFocus() }) {
                                Text("Done")
                            }
                        }
                    }

                )
            }
        ) { innerPadding -> run {
            BasicTextField(state,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = innerPadding.calculateTopPadding())
                    .fillMaxSize()
                    .onFocusEvent {
                        isFocused = it.isFocused
                    })
        }

        }
    }
    detailsComponent()





}