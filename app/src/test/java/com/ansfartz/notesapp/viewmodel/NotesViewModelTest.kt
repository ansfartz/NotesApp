package com.ansfartz.notesapp.viewmodel

import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: NotesViewModel

    @Before
    fun setUp() {
        viewModel = NotesViewModel()
    }

    // --- Initial state ---

    @Test
    fun `initial uiState contains five sample notes`() {
        val notes = viewModel.uiState.value.notes
        assertEquals(5, notes.size)
    }

    @Test
    fun `sample notes have IDs 1 through 5`() {
        val ids = viewModel.uiState.value.notes.map { it.id }.sorted()
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), ids)
    }

    // --- getNoteById ---

    @Test
    fun `getNoteById returns correct note`() {
        val note = viewModel.getNoteById(1L)
        assertNotNull(note)
        assertEquals("Grocery List", note!!.title)
    }

    @Test
    fun `getNoteById returns null for nonexistent ID`() {
        assertNull(viewModel.getNoteById(999L))
    }

    // --- saveNote (create) ---

    @Test
    fun `saveNote appends new note when ID does not exist`() {
        val newNote = Note(id = 100L, title = "New", body = "Body", color = NoteColors.all[0])
        viewModel.saveNote(newNote)

        val notes = viewModel.uiState.value.notes
        assertEquals(6, notes.size)
        assertEquals(newNote, notes.last())
    }

    @Test
    fun `saveNote new note is retrievable by ID`() {
        val newNote = Note(id = 200L, title = "Findable", body = "", color = NoteColors.all[1])
        viewModel.saveNote(newNote)

        val found = viewModel.getNoteById(200L)
        assertNotNull(found)
        assertEquals("Findable", found!!.title)
    }

    // --- saveNote (update / upsert) ---

    @Test
    fun `saveNote updates existing note in place`() {
        val updated = Note(id = 1L, title = "Updated Grocery List", body = "Apples", color = NoteColors.all[2])
        viewModel.saveNote(updated)

        val notes = viewModel.uiState.value.notes
        assertEquals(5, notes.size)
        val note = viewModel.getNoteById(1L)
        assertEquals("Updated Grocery List", note!!.title)
        assertEquals("Apples", note.body)
    }

    @Test
    fun `saveNote update preserves list order`() {
        val originalIds = viewModel.uiState.value.notes.map { it.id }
        val updated = Note(id = 3L, title = "Updated Ideas", body = "New body", color = NoteColors.all[0])
        viewModel.saveNote(updated)

        assertEquals(originalIds, viewModel.uiState.value.notes.map { it.id })
    }

    // --- deleteNote ---

    @Test
    fun `deleteNote removes note by ID`() {
        viewModel.deleteNote(1L)

        val notes = viewModel.uiState.value.notes
        assertEquals(4, notes.size)
        assertNull(viewModel.getNoteById(1L))
    }

    @Test
    fun `deleteNote with nonexistent ID does not change list`() {
        val sizeBefore = viewModel.uiState.value.notes.size
        viewModel.deleteNote(999L)
        assertEquals(sizeBefore, viewModel.uiState.value.notes.size)
    }

    @Test
    fun `deleting all notes results in empty list`() {
        listOf(1L, 2L, 3L, 4L, 5L).forEach { viewModel.deleteNote(it) }
        assertTrue(viewModel.uiState.value.notes.isEmpty())
    }

    // --- Combined operations ---

    @Test
    fun `save then delete then re-save produces correct state`() {
        val note = Note(id = 300L, title = "Temp", body = "", color = NoteColors.all[0])
        viewModel.saveNote(note)
        assertEquals(6, viewModel.uiState.value.notes.size)

        viewModel.deleteNote(300L)
        assertEquals(5, viewModel.uiState.value.notes.size)
        assertNull(viewModel.getNoteById(300L))

        val note2 = Note(id = 300L, title = "Recreated", body = "Back", color = NoteColors.all[1])
        viewModel.saveNote(note2)
        assertEquals(6, viewModel.uiState.value.notes.size)
        assertEquals("Recreated", viewModel.getNoteById(300L)!!.title)
    }
}
