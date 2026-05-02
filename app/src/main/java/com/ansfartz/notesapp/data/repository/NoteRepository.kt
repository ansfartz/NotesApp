package com.ansfartz.notesapp.data.repository

import com.ansfartz.notesapp.data.Note
import kotlinx.coroutines.flow.Flow

/**
 * Contract for note data access.  The ViewModel depends on this
 * interface — not a concrete class — so implementations can be
 * swapped freely (Room/SQLite, remote API, test fakes).
 *
 * Uses [Flow] (not StateFlow) so each implementation can provide its
 * own reactive stream — Room returns a Flow backed by SQLite triggers.
 * The ViewModel converts it to StateFlow via [stateIn].
 *
 * Write methods are [suspend] so database-backed implementations can
 * run on background threads without blocking the caller.
 */
interface NoteRepository {
    val notes: Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(id: Long)
}
