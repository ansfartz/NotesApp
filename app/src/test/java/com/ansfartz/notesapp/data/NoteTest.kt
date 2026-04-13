package com.ansfartz.notesapp.data

import org.junit.Assert.*
import org.junit.Test

class NoteTest {

    @Test
    fun `default note has empty title and body`() {
        val note = Note(id = 1L)
        assertEquals("", note.title)
        assertEquals("", note.body)
    }

    @Test
    fun `default color is first in NoteColors palette`() {
        val note = Note(id = 1L)
        assertEquals(NoteColors.all.first(), note.color)
    }

    @Test
    fun `NoteColors palette has 8 entries`() {
        assertEquals(8, NoteColors.all.size)
    }

    @Test
    fun `NoteColors entries are all distinct`() {
        assertEquals(NoteColors.all.size, NoteColors.all.distinct().size)
    }

    @Test
    fun `notes with same properties are equal`() {
        val a = Note(id = 1L, title = "T", body = "B", color = NoteColors.all[0])
        val b = Note(id = 1L, title = "T", body = "B", color = NoteColors.all[0])
        assertEquals(a, b)
    }

    @Test
    fun `notes with different IDs are not equal`() {
        val a = Note(id = 1L, title = "T", body = "B", color = NoteColors.all[0])
        val b = Note(id = 2L, title = "T", body = "B", color = NoteColors.all[0])
        assertNotEquals(a, b)
    }
}
