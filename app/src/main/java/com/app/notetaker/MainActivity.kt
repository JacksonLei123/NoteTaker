package com.app.notetaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.app.notetaker.database.AppDatabase
import com.app.notetaker.database.NoteDao
import com.app.notetaker.mvc.NoteRepository
import com.app.notetaker.mvc.NoteViewModel
import com.app.notetaker.mvc.NoteViewModelFactory
import com.app.notetaker.openai.Client
import okhttp3.OkHttpClient
import java.lang.StrictMath.abs

sealed class EditorItem(open var text: String) {
    data class TextItem(override var text: String) : EditorItem(text)
    data class CheckboxItem(override var text: String, var isChecked: Boolean) : EditorItem(text)
}

class MainActivity : ComponentActivity() {
    private var dao: NoteDao? = null
    private var client: OkHttpClient? = null
    private var noteViewModel: NoteViewModel? = null
    private var noteRepository: NoteRepository? = null
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initVars()
        setContent {
            val notesState = noteViewModel!!.notes.collectAsState(emptyList())
//            Navigation(notesState, noteViewModel!!, noteRepository!!)
//            RichTextToolbar(isBold = false, isItalic = false, {}, { })


            @Composable
            fun MixedTextEditor() {
                var textFields = remember { mutableStateListOf<TextFieldValue>() }
                val focusRequesters = remember { mutableStateListOf<FocusRequester>() }
                val scrollState = rememberScrollState()
                val coroutineScope = rememberCoroutineScope()
                textFields.add(TextFieldValue("This is normal text."))
                var focusIndex by remember { mutableStateOf(-1) }
                var valueChanged by remember {mutableStateOf(0)}
                focusRequesters.add(FocusRequester())
                var items by remember {
                    mutableStateOf(
                        mutableListOf<EditorItem>(
                            EditorItem.TextItem("This is normal text."),
//                            EditorItem.CheckboxItem("Task 1", false),
//                            EditorItem.TextItem("Another normal text."),
//                            EditorItem.CheckboxItem("Task 2", true)
                        )
                    )
                }

                LaunchedEffect(focusIndex) {
                    println("LAUNCHED")
                    
                    if (focusIndex != -1 && focusIndex < focusRequesters.size) {
                        focusRequesters[focusIndex].requestFocus()
                        focusIndex = -1 // Reset after focusing
                    }
                }

                Column(modifier = Modifier.fillMaxSize().padding(16.dp).imePadding().verticalScroll(scrollState)) {

                    items.forEachIndexed {index, item ->
//                        var textFieldValue = when (item) {
//                            is EditorItem.TextItem -> remember { mutableStateOf(TextFieldValue(item.text)) }
//                            is EditorItem.CheckboxItem -> remember { mutableStateOf(TextFieldValue(item.text)) }
//                        }
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            if (item is EditorItem.CheckboxItem) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { isChecked ->
                                        item.isChecked = isChecked
                                    }
                                )
                            }

                            BasicTextField(
                                value = textFields[index],
//                                singleLine = true,
                                maxLines = 1,
                                onValueChange = { newText ->
                                    println("ON VALUE: ${newText.text}")
                                    println(newText.text.length)
                                    println(textFields[index].text.length)
                                    if (abs(newText.text.length - textFields[index].text.length) <= 1) {
                                        when (item) {
                                            is EditorItem.TextItem -> {textFields[index] = newText; items[index].text = newText.text}
                                            is EditorItem.CheckboxItem -> {textFields[index] = newText; items[index].text = newText.text}
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = if (item is EditorItem.CheckboxItem) 8.dp else 0.dp)
                                    .focusRequester(focusRequesters[index])
                                    .onKeyEvent { keyEvent ->
//                                        val index = items.indexOf(item)
                                        if (keyEvent.type == KeyEventType.KeyUp) {
                                            when (keyEvent.key) {
                                                Key.Enter -> {
                                                    var selection = textFields[index].selection.start
                                                    var textLength = textFields[index].text.length
                                                    var firstHalf = textFields[index].text.substring(0, selection)
                                                    var secondHalf = textFields[index].text.substring(selection, textLength)
                                                    items[index].text = firstHalf
                                                    textFields[index] = TextFieldValue(text = firstHalf, selection = TextRange(selection))
                                                    items = items.apply {
                                                        add(
                                                            index + 1,
                                                            if (item is EditorItem.CheckboxItem) {
                                                                EditorItem.CheckboxItem(
                                                                    secondHalf, false
                                                                )
                                                            }else {
                                                                EditorItem.TextItem(
                                                                    secondHalf
                                                                )

                                                            }
                                                        )
                                                    }

                                                    textFields.add(index + 1, TextFieldValue(secondHalf))
                                                    val newFocusRequester = FocusRequester()
                                                    focusRequesters.add(index + 1, newFocusRequester)
                                                    focusIndex = index + 1
                                                    true
                                                }
                                                Key.Backspace -> {
                                                    println(textFields[index].text)
                                                    println(items[index].text)
                                                    if (index > 0 && textFields[index].selection.start == 0) {
                                                        textFields[index - 1] = TextFieldValue(items[index - 1].text + items[index].text, selection = TextRange(items[index-1].text.length))
                                                        items[index - 1].text += items[index].text
                                                        focusRequesters.removeAt(index)
                                                        items.removeAt(index)
                                                        textFields.removeAt(index)
                                                        focusIndex = index - 1
                                                    }
                                                    true
                                                }
                                                else -> false
                                            }
                                        } else false
                                    }
                            )
                        }
                    }
                }
            }
            MixedTextEditor()
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }

    }

    private fun initVars() {
        dao = AppDatabase.getDatabase(this).noteDao()
        noteRepository = NoteRepository(dao!!)
        client = Client.getHTTPClient()
        noteViewModel = NoteViewModelFactory(noteRepository!!).create(NoteViewModel::class.java)
    }


}
