package com.app.notetaker.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.notetaker.media.DocScan
import com.app.notetaker.mvc.Note
import com.app.notetaker.mvc.NoteDetailViewModel
import com.app.notetaker.mvc.NoteViewModel
import com.app.notetaker.mvc.UiState
import com.app.notetaker.openai.ChatResponse
import com.app.notetaker.ui.textEditor.RichTextField
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun getCurrentActivity(): Activity? {
    val context = LocalContext.current
    return context.findActivity()
}
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(
    navController: NavController,
    notesState: State<List<Note>>,
    noteViewModel: NoteViewModel,
    noteDetailViewModel: NoteDetailViewModel,
    param: String
) {
    val note = notesState.value.find {it.uid == noteDetailViewModel.noteId}
    val state = rememberTextFieldState(note!!.notes!!)

    val aiSummaryLoadingState by noteDetailViewModel.aiSummaryApiState.collectAsState()
    fun navigateBack() {
//        if (param.isNotEmpty()) {
//            noteViewModel.updateNote(param.toInt(), state.text.toString())
//        } else {
//            noteViewModel.createNote(state.text.toString())
//        }
        noteViewModel.updateNote(param.toInt(), state.text.toString())
        navController.navigate(Screen.MainScreen.route)
    }
    @Composable
    fun detailsComponent() {
        val context = LocalContext.current
        val mainActivity = getCurrentActivity()
        val focusManager = LocalFocusManager.current
        var isFocused by remember { mutableStateOf(false) }
        val scanClient = DocScan.getScannerClient()
        val topAppBarHeight = 55.dp
        val bottomToolBarHeight = 40.dp
        val coroutineScope = rememberCoroutineScope()
        var pages by remember { mutableIntStateOf(0) }
        val scannerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = {
                if (it.resultCode == RESULT_OK) {
                    val resultIntent = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                    val uri = resultIntent?.pages?.get(0)?.imageUri
                    val uriArray = resultIntent?.pages
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    var string = ""
                    uriArray?.forEach { uriLink ->
                        val image = InputImage.fromFilePath(context, uriLink.imageUri)
                        val resultText = recognizer.process(image)
                            .addOnSuccessListener { result ->
                                pages += 1
                                // Task completed successfully
                                string += result.text
                                string += "\n"
                                if (pages == uriArray.size) {
                                    noteDetailViewModel.requestAISummary(string)
                                }
                                val resultText = result.text
                                for (block in result.textBlocks) {
                                    val blockText = block.text
                                    val blockCornerPoints = block.cornerPoints
                                    val blockFrame = block.boundingBox
                                    for (line in block.lines) {
                                        val lineText = line.text
                                        val lineCornerPoints = line.cornerPoints
                                        val lineFrame = line.boundingBox
                                        for (element in line.elements) {
                                            val elementText = element.text
                                            string += " $elementText"
                                            val elementCornerPoints = element.cornerPoints
                                            val elementFrame = element.boundingBox
                                        }
                                    }
                                }

                            }
                            .addOnFailureListener { e ->
                                println("HELO FAILURE")
                                // Task failed with an exception
                                // ...
                            }
                    }
                }
            }
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth().height(topAppBarHeight),
                    navigationIcon = {
                        IconButton(modifier = Modifier.size(topAppBarHeight), onClick = { focusManager.clearFocus(); navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    },
                    title = {},
                    actions = {
                        if (isFocused) {
                            TextButton(onClick = { focusManager.clearFocus() }) {
                                Text("Done", color = Color.Black, fontSize = 15.sp)
                            }
                        } else {
                            IconButton(onClick = {
                                scanClient.getStartScanIntent(mainActivity!!)
                                    .addOnSuccessListener {
                                        scannerLauncher.launch(
                                            IntentSenderRequest.Builder(it).build()
                                        )
                                    }
                                    .addOnFailureListener {
                                        println("FAILED")
                                    }
                            }) {
                                Icon(
                                    Icons.Outlined.CameraAlt,
                                    contentDescription = "Scan a document",
                                    modifier = Modifier
                                )
                            }
                        }
                    }

                )
            },
            bottomBar = {
                if (isFocused) {
//                    RichTextToolbar(isBold = false, isItalic = false, onBoldToggle = {println("HELLO")}, modifier = Modifier.height(bottomToolBarHeight))
                }
            }
        ) { innerPadding -> run {

            val textFieldHeight = remember(isFocused) {
                if (isFocused) 300.dp else 1000.dp
            }
            when (aiSummaryLoadingState) {
                is UiState.Loading -> Text("Generating AI summary...", modifier = Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = innerPadding.calculateTopPadding()
                    )
                )
                is UiState.Success -> {
                    val summary = (aiSummaryLoadingState as UiState.Success<ChatResponse>).data.choices.firstOrNull()?.message?.content
                    coroutineScope.launch {
                        summary!!.forEachIndexed { index, _ -> run {
                            state.edit {
                                append(summary[index])
                            }
                            delay(10)

                        }
                        }
                        state.edit {
                            append("\n\n")
                        }
                    }

                    RichTextField(state, topInset = topAppBarHeight, bottomInset = bottomToolBarHeight, modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = innerPadding.calculateTopPadding()
                        )
                        .fillMaxSize()
                        .onFocusEvent {
                            isFocused = it.hasFocus
                        })
                    noteDetailViewModel.resetApiState()

                }
                is UiState.Idle -> {
                    RichTextField(state, modifier = Modifier
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = innerPadding.calculateTopPadding()
                        )
                        .fillMaxSize()
                        .onFocusEvent {
                            isFocused = it.hasFocus
                        }, topInset = topAppBarHeight, bottomToolBarHeight
                    )
                }
                is UiState.Error -> println("ERROR")

            }

        }

        }
    }
    detailsComponent()



}