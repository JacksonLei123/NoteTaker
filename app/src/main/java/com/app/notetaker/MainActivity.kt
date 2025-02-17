package com.app.notetaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import com.app.notetaker.database.AppDatabase
import com.app.notetaker.database.NoteDao
import com.app.notetaker.mvc.NoteViewModel
import com.app.notetaker.mvc.NoteViewModelFactory
import com.app.notetaker.navigation.Navigation
import com.app.notetaker.openai.Client
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    private var dao: NoteDao? = null
    private var client: OkHttpClient? = null
    private var noteViewModel: NoteViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initVars()
        setContent {
            val notesState = noteViewModel!!.notes.collectAsState(emptyList())
            Navigation(notesState, noteViewModel!!)
        }

    }

    private fun initVars() {
        dao = AppDatabase.getDatabase(this).noteDao()
        client = Client.getHTTPClient()
        noteViewModel = NoteViewModelFactory(dao!!).create(NoteViewModel::class.java)
    }


}
