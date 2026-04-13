package com.ansfartz.notesapp.ui.navigation

import org.junit.Assert.*
import org.junit.Test

class ScreenTest {

    @Test
    fun `NotesList route is notes_list`() {
        assertEquals("notes_list", Screen.NotesList.route)
    }

    @Test
    fun `NoteEdit route contains noteId placeholder`() {
        assertEquals("note_edit/{noteId}", Screen.NoteEdit.route)
    }

    @Test
    fun `createRoute with specific ID substitutes correctly`() {
        assertEquals("note_edit/42", Screen.NoteEdit.createRoute(42L))
    }

    @Test
    fun `createRoute default is new-note sentinel`() {
        assertEquals("note_edit/-1", Screen.NoteEdit.createRoute())
    }

    @Test
    fun `createRoute with zero ID`() {
        assertEquals("note_edit/0", Screen.NoteEdit.createRoute(0L))
    }
}
