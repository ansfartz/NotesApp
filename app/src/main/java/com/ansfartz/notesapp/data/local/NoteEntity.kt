package com.ansfartz.notesapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ansfartz.notesapp.data.Note

/**
 * Room entity that maps to the "notes" table. Kept separate from the domain [Note] class so
 * the database schema doesn't leak into the rest of the app.
 *
 * Use [NoteEntity.toNote] and [Note.toEntity] to convert at the repository boundary.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val body: String,
    val color: Int,
)

fun NoteEntity.toNote(): Note = Note(id = id, title = title, body = body, color = color)

fun Note.toEntity(): NoteEntity = NoteEntity(id = id, title = title, body = body, color = color)
