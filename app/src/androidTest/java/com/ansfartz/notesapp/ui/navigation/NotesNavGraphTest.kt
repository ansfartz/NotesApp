package com.ansfartz.notesapp.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.ansfartz.notesapp.ui.theme.NotesAppTheme
import org.junit.Rule
import org.junit.Test

class NotesNavGraphTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun app_startsOnNotesListScreen() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithText("My Notes").assertIsDisplayed()
    }

    @Test
    fun fab_navigatesToNewNoteScreen() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithContentDescription("Add note").performClick()
        composeRule.onNodeWithText("New Note").assertIsDisplayed()
    }

    @Test
    fun noteClick_navigatesToEditScreen() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithText("Grocery List").performClick()
        composeRule.onNodeWithText("Edit Note").assertIsDisplayed()
        composeRule.onNodeWithText("Grocery List").assertIsDisplayed()
    }

    @Test
    fun backButton_returnsToNotesList() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithContentDescription("Add note").performClick()
        composeRule.onNodeWithText("New Note").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("My Notes").assertIsDisplayed()
    }

    @Test
    fun createNote_appearsInList() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithContentDescription("Add note").performClick()
        composeRule.onNodeWithText("Title").performTextInput("Brand New Note")
        composeRule.onNodeWithContentDescription("Save").performClick()
        composeRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeRule.onNodeWithText("Brand New Note").assertIsDisplayed()
    }

    @Test
    fun deleteNote_removesFromList() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithText("Grocery List").assertIsDisplayed()
        composeRule.onNodeWithText("Grocery List").performClick()
        composeRule.onNodeWithContentDescription("Delete").performClick()
        composeRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeRule.onNodeWithText("Grocery List").assertDoesNotExist()
    }

    @Test
    fun editNote_updatesInList() {
        composeRule.setContent { NotesAppTheme { NotesNavGraph() } }
        composeRule.onNodeWithText("Grocery List").performClick()
        composeRule.onNodeWithText("Grocery List").performTextClearance()
        composeRule.onNodeWithText("Title").performTextInput("Updated Groceries")
        composeRule.onNodeWithContentDescription("Save").performClick()
        composeRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeRule.onNodeWithText("Updated Groceries").assertIsDisplayed()
    }
}
