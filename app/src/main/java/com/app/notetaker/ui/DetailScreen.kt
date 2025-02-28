package com.app.notetaker.ui

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.notetaker.media.DocScan
import com.app.notetaker.mvc.Note
import com.app.notetaker.mvc.NoteViewModel
import com.google.mlkit.vision.common.InputImage

import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay


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
        val context = LocalContext.current
        val mainActivity = getCurrentActivity()
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        var isFocused by remember { mutableStateOf(false) }
        val scanClient = DocScan.getScannerClient()
        val scannerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = {
                if (it.resultCode == RESULT_OK) {
                    val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                    val uri = result?.pages?.get(0)?.imageUri
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val image = InputImage.fromFilePath(context, uri!!)
                    val resultText = recognizer.process(image)
                        .addOnSuccessListener { result ->
                            // Task completed successfully
                            var string = ""
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
                            state.edit {
                                append(string)
                            }
                        }
                        .addOnFailureListener { e ->
                            println("HELO FAILURE")
                            // Task failed with an exception
                            // ...
                        }
//                    val filePath = result?.pdf?.uri?.path
//                    val projectId = "stor320-291615"
//                    val location = "us" // Format is "us" or "eu".
//                    val processorId = "227293a4070154ae"
//
//                    val credentials = GoogleCredentials.fromStream(FileInputStream("/application_default_credentials.json"))
//                    val endpoint = java.lang.String.format("%s-documentai.googleapis.com:443", location)
//                    val settings: DocumentProcessorServiceSettings =
//                        DocumentProcessorServiceSettings.newBuilder()
////                            .setCredentialsProvider()
//                            .setEndpoint(endpoint)
//                            .build()
//                    val client = DocumentProcessorServiceClient.create(settings)
//                    val name =
//                        java.lang.String.format(
//                            "projects/%s/locations/%s/processors/%s",
//                            projectId,
//                            location,
//                            processorId
//                        )
//                    val imageFileData = Files.readAllBytes(Paths.get(filePath))
//                    println(imageFileData)


                }

            }
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { focusManager.clearFocus(); navigateBack() }) {
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
            },
            bottomBar = {
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
                                onClick = {
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
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(35.dp)
                                )
                            }
                        }
                    }
                )

            }
        ) { innerPadding -> run {
            BasicTextField(state,
                modifier = Modifier
                    .padding(
                        start = 26.dp,
                        end = 26.dp,
                        top = innerPadding.calculateTopPadding(),
                        bottom = 10.dp
                    )
                    .focusRequester(focusRequester)
                    .imePadding()
                    .fillMaxSize()
                    .onFocusEvent {
                        isFocused = it.isFocused
                    })
        }

        }
        LaunchedEffect(Unit) {
            if (state.text.isEmpty()) {
                run {
                    delay(300)
                }
                focusRequester.requestFocus()
            }

        }
    }
    detailsComponent()





}