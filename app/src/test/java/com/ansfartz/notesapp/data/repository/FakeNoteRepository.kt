package com.ansfartz.notesapp.data.repository

import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.createSampleNotes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Test-only [NoteRepository] backed by a [MutableStateFlow]. */
class FakeNoteRepository : NoteRepository {

    private val _notes = MutableStateFlow(createSampleNotes())
    override val notes: Flow<List<Note>> = _notes.asStateFlow()

    override suspend fun getNoteById(id: Long): Note? =
        _notes.value.find { it.id == id }

    override suspend fun saveNote(note: Note) {
        _notes.update { current ->
            val index = current.indexOfFirst { it.id == note.id }
            if (index >= 0) {
                current.toMutableList().apply { this[index] = note }
            } else {
                current + note
            }
        }
    }

    override suspend fun deleteNote(id: Long) {
        _notes.update { current -> current.filter { it.id != id } }
    }
}
