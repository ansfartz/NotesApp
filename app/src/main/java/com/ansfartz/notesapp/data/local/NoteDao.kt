package com.ansfartz.notesapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for [NoteEntity].
 *
 * [observeAll] returns a reactive [Flow] that re-emits whenever the "notes" table changes - Room
 * handles the SQLite trigger wiring automatically.
 *
 * Write methods are [suspend] so they run on a background thread via Room's built-in coroutine support.
 */
@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): NoteEntity?

    @Upsert
    suspend fun upsert(entity: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
