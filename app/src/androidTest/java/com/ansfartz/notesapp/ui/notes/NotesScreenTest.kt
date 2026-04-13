package com.ansfartz.notesapp.ui.notes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import com.ansfartz.notesapp.ui.theme.NotesAppTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NotesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleNotes = listOf(
        Note(id = 1L, title = "Grocery List", body = "Eggs\nMilk", color = NoteColors.all[0]),
        Note(id = 2L, title = "Meeting Notes", body = "Discuss roadmap", color = NoteColors.all[1]),
        Note(id = 3L, title = "", body = "Body-only note", color = NoteColors.all[2]),
    )

    // --- Note list display ---

    @Test
    fun topBar_displaysTitle() {
        composeRule.setContent {
            NotesAppTheme { NotesScreen(notes = sampleNotes, onNoteClick = {}, onAddNote = {}) }
        }
        composeRule.onNodeWithText("My Notes").assertIsDisplayed()
    }

    @Test
    fun noteCards_displayTitleAndBody() {
        composeRule.setContent {
            NotesAppTheme { NotesScreen(notes = sampleNotes, onNoteClick = {}, onAddNote = {}) }
        }
        composeRule.onNodeWithText("Grocery List").assertIsDisplayed()
        composeRule.onNodeWithText("Discuss roadmap").assertIsDisplayed()
    }

    @Test
    fun noteWithBlankTitle_displaysOnlyBody() {
        composeRule.setContent {
            NotesAppTheme { NotesScreen(notes = sampleNotes, onNoteClick = {}, onAddNote = {}) }
        }
        composeRule.onNodeWithText("Body-only note").assertIsDisplayed()
    }

    @Test
    fun fab_isDisplayed() {
        composeRule.setContent {
            NotesAppTheme { NotesScreen(notes = sampleNotes, onNoteClick = {}, onAddNote = {}) }
        }
        composeRule.onNodeWithContentDescription("Add note").assertIsDisplayed()
    }

    // --- Empty state ---

    @Test
    fun emptyList_showsEmptyStateMessage() {
        composeRule.setContent {
            NotesAppTheme { NotesScreen(notes = emptyList(), onNoteClick = {}, onAddNote = {}) }
        }
        composeRule.onNodeWithText("No notes yet.\nTap + to create one!").assertIsDisplayed()
    }

    @Test
    fun emptyList_stillShowsFab() {
        composeRule.setContent {
            NotesAppTheme { NotesScreen(notes = emptyList(), onNoteClick = {}, onAddNote = {}) }
        }
        composeRule.onNodeWithContentDescription("Add note").assertIsDisplayed()
    }

    // --- Click callbacks ---

    @Test
    fun fab_click_triggersOnAddNote() {
        var addClicked = false
        composeRule.setContent {
            NotesAppTheme {
                NotesScreen(notes = sampleNotes, onNoteClick = {}, onAddNote = { addClicked = true })
            }
        }
        composeRule.onNodeWithContentDescription("Add note").performClick()
        assertTrue(addClicked)
    }

    @Test
    fun noteCard_click_triggersOnNoteClickWithCorrectId() {
        var clickedId: Long? = null
        composeRule.setContent {
            NotesAppTheme {
                NotesScreen(notes = sampleNotes, onNoteClick = { clickedId = it }, onAddNote = {})
            }
        }
        composeRule.onNodeWithText("Grocery List").performClick()
        assertEquals(1L, clickedId)
    }
}
