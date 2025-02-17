package com.app.notetaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.notetaker.mvc.Note
import com.app.notetaker.mvc.NoteViewModel

@Composable
fun Navigation(notesState: State<List<Note>>, noteViewModel: NoteViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController =navController, notesState, noteViewModel)
        }
        composable(
            route = Screen.DetailScreen.route,
            arguments = listOf(
                navArgument("param") {
                    type = NavType.StringType
                }
            )
        ) {
            val param = it.arguments?.getString("param") ?: ""
            DetailScreen(navController=navController, notesState, noteViewModel, param)
        }
    }
}
