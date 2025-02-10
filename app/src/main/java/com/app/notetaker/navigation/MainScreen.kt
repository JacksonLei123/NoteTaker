package com.app.notetaker.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {

    @Composable
    fun GreetingPreview() {
        fun addNote() {
            navController.navigate(Screen.DetailScreen.route)
//        lifecycleScope.launch(Dispatchers.IO) {
//            var note = Note("sdfs")
//            dao?.insertNote(note)
//        }
        }
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun ScaffoldExample() {

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text("Notes")
                        }
                    )
                },
                floatingActionButton = {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        FloatingActionButton(onClick = {}) {
                            Icon(Icons.Filled.Info, "Floating action button.")
                        }
                        FloatingActionButton(onClick = { addNote() }) {
                            Icon(Icons.Filled.Add, "Floating action button.")
                        }

                    }

                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(listOf("Movies to watch", "Homework")) { index ->
                        val interactionSource = remember { MutableInteractionSource() }
                        ListItem(
                            headlineContent = {
                                Text(text = "$index")
                            },
                            modifier = Modifier.clickable(
                                onClick = {},
                                indication = rememberRipple(),
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
        ScaffoldExample()


    }
    GreetingPreview()

}
