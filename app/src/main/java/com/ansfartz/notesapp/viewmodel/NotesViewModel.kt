package com.ansfartz.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.repository.InMemoryNoteRepository
import com.ansfartz.notesapp.data.repository.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Bridges the data layer ([NoteRepository]) and the UI layer (screens).
 * Transforms repository data into [NotesUiState] and exposes it as a
 * reactive [StateFlow].  The ViewModel never knows about Compose or
 * Android framework UI classes.
 */
class NotesViewModel(
    private val repository: NoteRepository = InMemoryNoteRepository(),
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = repository.notes
        .map { notes -> NotesUiState(notes = notes) }
        .stateIn(
            scope = viewModelScope,
            // Eagerly: the flow runs from creation, so the ViewModel always
            // holds the latest state — simpler to test and reason about.
            // In larger apps you might use WhileSubscribed(5_000) to stop
            // collecting when no UI is observing, saving resources.
            started = SharingStarted.Eagerly,
            initialValue = NotesUiState(),
        )

    /** Reads from the already-collected UI state — no database hit. */
    fun getNoteById(id: Long): Note? =
        uiState.value.notes.find { it.id == id }

    fun saveNote(note: Note) {
        viewModelScope.launch { repository.saveNote(note) }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch { repository.deleteNote(id) }
    }
}
