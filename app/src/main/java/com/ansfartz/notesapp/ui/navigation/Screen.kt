package com.ansfartz.notesapp.ui.navigation

sealed class Screen(val route: String) {
    data object NotesList : Screen("notes_list")
    data object NoteEdit : Screen("note_edit/{noteId}") {
        fun createRoute(noteId: Long = -1L) = "note_edit/$noteId"
    }
}
