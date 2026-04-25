package com.ansfartz.notesapp.data.repository

import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NoteRepositoryTest {

    private lateinit var repository: InMemoryNoteRepository

    @Before
    fun setUp() {
        repository = InMemoryNoteRepository()
    }

    // --- Initial state ---

    @Test
    fun `initial state contains five sample notes`() = runTest {
        assertEquals(5, repository.notes.first().size)
    }

    @Test
    fun `sample notes have IDs 1 through 5`() = runTest {
        val ids = repository.notes.first().map { it.id }.sorted()
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), ids)
    }

    // --- getNoteById ---

    @Test
    fun `getNoteById returns correct note`() = runTest {
        val note = repository.getNoteById(1L)
        assertNotNull(note)
        assertEquals("Grocery List", note!!.title)
    }

    @Test
    fun `getNoteById returns null for nonexistent ID`() = runTest {
        assertNull(repository.getNoteById(999L))
    }

    // --- saveNote (create) ---

    @Test
    fun `saveNote appends new note when ID does not exist`() = runTest {
        val newNote = Note(id = 100L, title = "New", body = "Body", color = NoteColors.all[0])
        repository.saveNote(newNote)

        val notes = repository.notes.first()
        assertEquals(6, notes.size)
        assertEquals(newNote, notes.last())
    }

    @Test
    fun `saveNote new note is retrievable by ID`() = runTest {
        val newNote = Note(id = 200L, title = "Findable", body = "", color = NoteColors.all[1])
        repository.saveNote(newNote)

        val found = repository.getNoteById(200L)
        assertNotNull(found)
        assertEquals("Findable", found!!.title)
    }

    // --- saveNote (update / upsert) ---

    @Test
    fun `saveNote updates existing note in place`() = runTest {
        val updated = Note(id = 1L, title = "Updated Grocery List", body = "Apples", color = NoteColors.all[2])
        repository.saveNote(updated)

        val notes = repository.notes.first()
        assertEquals(5, notes.size)
        val note = repository.getNoteById(1L)
        assertEquals("Updated Grocery List", note!!.title)
        assertEquals("Apples", note.body)
    }

    @Test
    fun `saveNote update preserves list order`() = runTest {
        val originalIds = repository.notes.first().map { it.id }
        val updated = Note(id = 3L, title = "Updated Ideas", body = "New body", color = NoteColors.all[0])
        repository.saveNote(updated)

        assertEquals(originalIds, repository.notes.first().map { it.id })
    }

    // --- deleteNote ---

    @Test
    fun `deleteNote removes note by ID`() = runTest {
        repository.deleteNote(1L)

        val notes = repository.notes.first()
        assertEquals(4, notes.size)
        assertNull(repository.getNoteById(1L))
    }

    @Test
    fun `deleteNote with nonexistent ID does not change list`() = runTest {
        val sizeBefore = repository.notes.first().size
        repository.deleteNote(999L)
        assertEquals(sizeBefore, repository.notes.first().size)
    }

    @Test
    fun `deleting all notes results in empty list`() = runTest {
        listOf(1L, 2L, 3L, 4L, 5L).forEach { repository.deleteNote(it) }
        assertTrue(repository.notes.first().isEmpty())
    }

    // --- StateFlow reactivity ---

    @Test
    fun `notes Flow emits updated list after save`() = runTest {
        val newNote = Note(id = 100L, title = "Reactive", body = "", color = NoteColors.all[0])
        repository.saveNote(newNote)

        assertTrue(repository.notes.first().any { it.id == 100L })
    }

    @Test
    fun `notes Flow emits updated list after delete`() = runTest {
        repository.deleteNote(1L)
        assertFalse(repository.notes.first().any { it.id == 1L })
    }
}
