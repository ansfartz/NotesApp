package com.ansfartz.notesapp.viewmodel

import com.ansfartz.notesapp.data.Note

/**
 * Represents the UI state for the notes list screen.
 * The ViewModel exposes a single [NotesUiState] so the View only
 * needs to observe one stream of data.
 */
data class NotesUiState(
    val notes: List<Note> = emptyList(),
)
