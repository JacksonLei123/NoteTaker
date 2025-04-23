package com.app.notetaker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.notetaker.mvc.Note
import com.app.notetaker.mvc.NoteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    notesState: State<List<Note>>,
    noteViewModel: NoteViewModel
) {
    @Composable
    fun NotesPage() {
        var selectedNotes = remember { mutableSetOf<Int>() }
        var selected = remember { mutableStateOf(false)}
        var size = remember { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()
        fun addNote() {
            coroutineScope.launch {
                noteViewModel.createNote("")
                delay(50)
                navController.navigate(Screen.DetailScreen.route.replace("{param}", notesState.value.first().uid.toString()))
            }
        }
        fun viewNote(uid: Int) {
            navController.navigate(Screen.DetailScreen.route.replace("{param}", uid.toString()))
        }
        fun selectNote(uid: Int) {
            selected.value = true
            selectedNotes.add(uid)
            size.intValue = selectedNotes.size
        }
        fun unSelectNote(uid: Int) {
            selectedNotes.remove(uid)
            size.intValue = selectedNotes.size
        }

        fun deleteNotes() {
            selectedNotes.forEach { noteViewModel.deleteNote(it) }
            selected.value = false
            selectedNotes.clear()
        }

        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun NoteScaffold() {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            if (selected.value) {
                                Text("Selected: " + size.intValue)
                            } else {
                                Text("Notes")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    if (!selected.value) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            FloatingActionButton(onClick = {
                                addNote()
                            }, modifier = Modifier.offset(y = (-30).dp)) {
                                Icon(Icons.Filled.Add, "Floating action button.")
                            }

                        }
                    }
                },
                bottomBar = {
                    if (selected.value) {
                        BottomAppBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer ,
                            contentColor = MaterialTheme.colorScheme.primary,
                            content = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .size(100.dp),
                                        shape = RectangleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        onClick = { deleteNotes() }) {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .size(35.dp)
                                        )
                                    }
                                    Button(
                                        modifier = Modifier
                                            .weight(1f)
                                            .size(100.dp),
                                        shape = RectangleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        onClick = {
                                            selected.value = false
                                            selectedNotes.clear()
                                        }) {
                                        Text("Cancel",
                                            fontSize = 25.sp,
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        )

                    }

                }

            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(notesState.value) { note ->
                        val titles = getTitleSubtitle(note.notes!!)
                        val interactionSource = remember { MutableInteractionSource() }
                        val checked = remember { mutableStateOf(false) }
                        ListItem(
                            headlineContent = {
                                Text(text = titles[0])
                            },
                            supportingContent = {
                                Text(text = titles[1])
                            },
                            trailingContent = {
                                if (selected.value) {
                                    Checkbox(
                                        checked = checked.value,
                                        onCheckedChange = { isChecked ->
                                            checked.value = isChecked
                                            if (isChecked) selectNote(note.uid) else unSelectNote(note.uid)
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = {
                                        if (selected.value) {
                                            if (!checked.value) selectNote(note.uid) else unSelectNote(note.uid)
                                        } else {
                                            viewNote(note.uid)
                                        }
                                        checked.value = !checked.value
                                    },
                                    onLongClick = {
                                        checked.value = true
                                        selectNote(note.uid)
                                    },
                                    indication = ripple(),
                                    interactionSource = interactionSource
                                )
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 20.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                }

            }
        }
        NoteScaffold()

    }
    NotesPage()

}
