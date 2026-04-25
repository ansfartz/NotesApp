package com.ansfartz.notesapp.data.repository

import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory implementation of [NoteRepository].
 * Notes live only in process memory and are lost on process death.
 * Useful for prototyping and as a test fake.
 *
 * To persist notes, swap in [LocalNoteRepository] — the ViewModel
 * and UI stay unchanged because they depend only on [NoteRepository].
 */
class InMemoryNoteRepository : NoteRepository {

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

private fun createSampleNotes(): List<Note> = listOf(
    Note(
        id = 1L,
        title = "Grocery List",
        body = "Eggs\nMilk\nBread\nButter\nCheese",
        color = NoteColors.all[0],
    ),
    Note(
        id = 2L,
        title = "Meeting Notes",
        body = "Discuss Q2 roadmap\nReview design specs\nAssign sprint tasks",
        color = NoteColors.all[1],
    ),
    Note(
        id = 3L,
        title = "Ideas",
        body = "Build a notes app with Jetpack Compose",
        color = NoteColors.all[4],
    ),
    Note(
        id = 4L,
        title = "Workout Plan",
        body = "Monday: Chest & Triceps\nWednesday: Back & Biceps\nFriday: Legs & Shoulders",
        color = NoteColors.all[2],
    ),
    Note(
        id = 5L,
        title = "",
        body = "Remember to call Mom this weekend!",
        color = NoteColors.all[3],
    ),
)
