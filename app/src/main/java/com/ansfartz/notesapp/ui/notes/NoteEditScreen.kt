package com.ansfartz.notesapp.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import com.ansfartz.notesapp.ui.theme.NotesAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    existingNote: Note?,
    onSave: (Note) -> Unit,
    onDelete: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isNew = existingNote == null
    val noteId = remember { existingNote?.id ?: System.nanoTime() }

    var title by remember { mutableStateOf(existingNote?.title ?: "") }
    var body by remember { mutableStateOf(existingNote?.body ?: "") }
    var selectedColor by remember {
        mutableStateOf(existingNote?.color ?: NoteColors.all.random())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "New Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (!isNew) {
                        IconButton(onClick = {
                            onDelete(noteId)
                            onNavigateBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                    IconButton(onClick = {
                        if (title.isNotBlank() || body.isNotBlank()) {
                            onSave(
                                Note(
                                    id = noteId,
                                    title = title.trim(),
                                    body = body.trim(),
                                    color = selectedColor,
                                )
                            )
                        }
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(selectedColor)
                .padding(16.dp),
        ) {
            ColorPicker(
                colors = NoteColors.all,
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title") },
                textStyle = MaterialTheme.typography.headlineSmall,
                colors = transparentTextFieldColors(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color.Black.copy(alpha = 0.12f),
            )

            TextField(
                value = body,
                onValueChange = { body = it },
                placeholder = { Text("Write something…") },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = transparentTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

@Composable
private fun ColorPicker(
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        colors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 3.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                        } else {
                            Modifier.border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = CircleShape
                            )
                        }
                    )
                    .clickable { onColorSelected(color) },
            )
        }
    }
}

@Composable
private fun transparentTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
)

// region Previews

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NoteEditScreenNewPreview() {
    NotesAppTheme {
        NoteEditScreen(
            existingNote = null,
            onSave = {},
            onDelete = {},
            onNavigateBack = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NoteEditScreenEditPreview() {
    NotesAppTheme {
        NoteEditScreen(
            existingNote = Note(
                id = 1L,
                title = "Grocery List",
                body = "Eggs\nMilk\nBread\nButter",
                color = NoteColors.all[2],
            ),
            onSave = {},
            onDelete = {},
            onNavigateBack = {},
        )
    }
}

// endregion
