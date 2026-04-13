package com.ansfartz.notesapp.ui.notes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import com.ansfartz.notesapp.ui.theme.NotesAppTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NoteEditScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val existingNote = Note(
        id = 1L,
        title = "Grocery List",
        body = "Eggs\nMilk",
        color = NoteColors.all[0],
    )

    // --- New note mode ---

    @Test
    fun newNote_showsNewNoteTitle() {
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(existingNote = null, onSave = {}, onDelete = {}, onNavigateBack = {})
            }
        }
        composeRule.onNodeWithText("New Note").assertIsDisplayed()
    }

    @Test
    fun newNote_deleteButton_isNotDisplayed() {
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(existingNote = null, onSave = {}, onDelete = {}, onNavigateBack = {})
            }
        }
        composeRule.onNodeWithContentDescription("Delete").assertDoesNotExist()
    }

    @Test
    fun newNote_showsPlaceholders() {
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(existingNote = null, onSave = {}, onDelete = {}, onNavigateBack = {})
            }
        }
        composeRule.onNodeWithText("Title").assertIsDisplayed()
        composeRule.onNodeWithText("Write something…").assertIsDisplayed()
    }

    // --- Edit mode ---

    @Test
    fun editNote_showsEditNoteTitle() {
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(existingNote = existingNote, onSave = {}, onDelete = {}, onNavigateBack = {})
            }
        }
        composeRule.onNodeWithText("Edit Note").assertIsDisplayed()
    }

    @Test
    fun editNote_populatesExistingFields() {
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(existingNote = existingNote, onSave = {}, onDelete = {}, onNavigateBack = {})
            }
        }
        composeRule.onNodeWithText("Grocery List").assertIsDisplayed()
        composeRule.onNodeWithText("Eggs\nMilk").assertIsDisplayed()
    }

    @Test
    fun editNote_deleteButton_isDisplayed() {
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(existingNote = existingNote, onSave = {}, onDelete = {}, onNavigateBack = {})
            }
        }
        composeRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
    }

    // --- Save callback ---

    @Test
    fun save_withContent_triggersOnSave() {
        var savedNote: Note? = null
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(
                    existingNote = null,
                    onSave = { savedNote = it },
                    onDelete = {},
                    onNavigateBack = {},
                )
            }
        }
        composeRule.onNodeWithText("Title").performTextInput("My Title")
        composeRule.onNodeWithContentDescription("Save").performClick()
        assertNotNull(savedNote)
        assertEquals("My Title", savedNote!!.title)
    }

    @Test
    fun save_withBlankFields_doesNotTriggerOnSave() {
        var saveCalled = false
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(
                    existingNote = null,
                    onSave = { saveCalled = true },
                    onDelete = {},
                    onNavigateBack = {},
                )
            }
        }
        composeRule.onNodeWithContentDescription("Save").performClick()
        assertFalse(saveCalled)
    }

    @Test
    fun save_trimsWhitespace() {
        var savedNote: Note? = null
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(
                    existingNote = null,
                    onSave = { savedNote = it },
                    onDelete = {},
                    onNavigateBack = {},
                )
            }
        }
        composeRule.onNodeWithText("Title").performTextInput("  Padded Title  ")
        composeRule.onNodeWithContentDescription("Save").performClick()
        assertEquals("Padded Title", savedNote!!.title)
    }

    // --- Delete callback ---

    @Test
    fun delete_triggersOnDeleteWithCorrectId() {
        var deletedId: Long? = null
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(
                    existingNote = existingNote,
                    onSave = {},
                    onDelete = { deletedId = it },
                    onNavigateBack = {},
                )
            }
        }
        composeRule.onNodeWithContentDescription("Delete").performClick()
        assertEquals(1L, deletedId)
    }

    // --- Back navigation ---

    @Test
    fun backButton_triggersOnNavigateBack() {
        var backClicked = false
        composeRule.setContent {
            NotesAppTheme {
                NoteEditScreen(
                    existingNote = null,
                    onSave = {},
                    onDelete = {},
                    onNavigateBack = { backClicked = true },
                )
            }
        }
        composeRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(backClicked)
    }
}
