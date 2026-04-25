package com.ansfartz.notesapp.data.repository

import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.local.NoteDao
import com.ansfartz.notesapp.data.local.toEntity
import com.ansfartz.notesapp.data.local.toNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room/SQLite-backed implementation of [NoteRepository].
 * Notes are persisted to disk and survive process death.
 *
 * [notes] returns a Room-managed [Flow] that automatically re-emits
 * whenever the underlying table changes — no manual refresh needed.
 */
class LocalNoteRepository(
    private val dao: NoteDao,
) : NoteRepository {

    override val notes: Flow<List<Note>> =
        dao.observeAll().map { entities -> entities.map { it.toNote() } }

    override suspend fun getNoteById(id: Long): Note? =
        dao.getById(id)?.toNote()

    override suspend fun saveNote(note: Note) =
        dao.upsert(note.toEntity())

    override suspend fun deleteNote(id: Long) =
        dao.deleteById(id)
}
