package com.app.notetaker.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.notetaker.mvc.Note
import com.app.notetaker.mvc.NoteDetailViewModel
import com.app.notetaker.mvc.NoteDetailViewModelFactory
import com.app.notetaker.mvc.NoteRepository
import com.app.notetaker.mvc.NoteViewModel

@Composable
fun Navigation(notesState: State<List<Note>>, noteViewModel: NoteViewModel, noteRepository: NoteRepository) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route, enterTransition = { slideInHorizontally(initialOffsetX = {-3000}) }) {
            MainScreen(navController =navController, notesState, noteViewModel)

        }
        composable(
            route = Screen.DetailScreen.route,
            arguments = listOf(
                navArgument("param") {
                    type = NavType.StringType
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = {3000}) }
        ) {
            val param = it.arguments?.getString("param") ?: ""
            val noteDetailViewModel: NoteDetailViewModel = viewModel(factory = NoteDetailViewModelFactory(param.toInt()))
            DetailScreen(navController=navController, notesState, noteViewModel, noteDetailViewModel, param)
        }
    }
}
