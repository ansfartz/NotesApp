package com.ansfartz.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ansfartz.notesapp.data.repository.NoteRepository

/**
 * Factory that injects a [NoteRepository] into [NotesViewModel].
 * Needed because Compose's default `viewModel()` can only call the
 * no-arg constructor.  With Hilt you would replace this with
 * `@HiltViewModel` + `@Inject constructor`.
 */
class NotesViewModelFactory(
    private val repository: NoteRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return NotesViewModel(repository) as T
    }
}
