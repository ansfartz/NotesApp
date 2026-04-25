package com.ansfartz.notesapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ansfartz.notesapp.data.repository.NoteRepository
import com.ansfartz.notesapp.ui.notes.NoteEditScreen
import com.ansfartz.notesapp.ui.notes.NotesScreen
import com.ansfartz.notesapp.viewmodel.NotesViewModel
import com.ansfartz.notesapp.viewmodel.NotesViewModelFactory

@Composable
fun NotesNavGraph(
    noteRepository: NoteRepository,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val viewModel: NotesViewModel = viewModel(
        factory = NotesViewModelFactory(noteRepository),
    )

    NavHost(
        navController = navController,
        startDestination = Screen.NotesList.route,
        modifier = modifier,
    ) {
        composable(
            route = Screen.NotesList.route
        ) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NotesScreen(
                notes = uiState.notes,
                onNoteClick = { noteId ->
                    navController.navigate(Screen.NoteEdit.createRoute(noteId))
                },
                onAddNote = {
                    navController.navigate(Screen.NoteEdit.createRoute())
                },
            )
        }

        composable(
            route = Screen.NoteEdit.route,
            arguments = listOf(
                navArgument("noteId") { type = NavType.LongType }
            ),
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            val existingNote = if (noteId != -1L) viewModel.getNoteById(noteId) else null

            NoteEditScreen(
                existingNote = existingNote,
                onSave = viewModel::saveNote,
                onDelete = viewModel::deleteNote,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
