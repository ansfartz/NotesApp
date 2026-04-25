package com.ansfartz.notesapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ansfartz.notesapp.data.createSampleNotes

/**
 * Room database holding the "notes" table.
 *
 * On first creation, [SeedCallback] inserts sample notes so the app
 * isn't empty on the first launch.  On subsequent launches the existing
 * data is preserved.
 */
@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        fun create(context: Context): NotesDatabase =
            Room.databaseBuilder(context, NotesDatabase::class.java, "notes.db")
                .addCallback(SeedCallback())
                .build()
    }
}

/**
 * Inserts sample notes on first database creation only.
 * Uses raw [SupportSQLiteDatabase] because the DAO isn't available
 * inside a Room callback.
 */
private class SeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        createSampleNotes().forEach { note ->
            val values = ContentValues().apply {
                put("id", note.id)
                put("title", note.title)
                put("body", note.body)
                put("color", note.color)
            }
            db.insert("notes", SQLiteDatabase.CONFLICT_REPLACE, values)
        }
    }
}
