package com.ansfartz.notesapp

import android.app.Application
import com.ansfartz.notesapp.data.local.NotesDatabase
import com.ansfartz.notesapp.data.repository.LocalNoteRepository
import com.ansfartz.notesapp.data.repository.NoteRepository

/**
 * Application-level singleton that provides the [NoteRepository] used
 * throughout the app.  Acts as a simple dependency container — swap
 * implementations here without touching ViewModel or UI code.
 */
class NotesApplication : Application() {

    private val database by lazy { NotesDatabase.create(this) }

    /**
     * The single [NoteRepository] instance for the app.
     * Change this line to swap between implementations:
     *   - [com.ansfartz.notesapp.data.repository.LocalNoteRepository]      → persists notes to SQLite (default)
     *   - [com.ansfartz.notesapp.data.repository.InMemoryNoteRepository]   → notes live in memory only
     */
    val noteRepository: NoteRepository by lazy {
        LocalNoteRepository(database.noteDao())
    }
}
