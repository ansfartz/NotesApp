package com.ansfartz.notesapp.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ansfartz.notesapp.data.Note
import com.ansfartz.notesapp.data.NoteColors
import com.ansfartz.notesapp.ui.theme.NotesAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Notes") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        },
        modifier = modifier,
    ) { padding ->
        if (notes.isEmpty()) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                items(notes, key = { it.id }) { note ->
                    NoteCard(note = note, onClick = { onNoteClick(note.id) })
                }
            }
        }
    }
}

@Composable
internal fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(note.color)),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (note.body.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            if (note.body.isNotBlank()) {
                Text(
                    text = note.body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No notes yet.\nTap + to create one!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// region Previews

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotesScreenPreview() {
    NotesAppTheme {
        NotesScreen(
            notes = listOf(
                Note(id = 1, title = "Grocery List", body = "Eggs\nMilk\nBread", color = NoteColors.all[0]),
                Note(id = 2, title = "Meeting Notes", body = "Discuss Q2 roadmap", color = NoteColors.all[1]),
                Note(id = 3, title = "Ideas", body = "Build a notes app", color = NoteColors.all[4]),
                Note(id = 4, title = "", body = "Call Mom this weekend!", color = NoteColors.all[3]),
            ),
            onNoteClick = {},
            onAddNote = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NotesScreenEmptyPreview() {
    NotesAppTheme {
        NotesScreen(
            notes = emptyList(),
            onNoteClick = {},
            onAddNote = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteCardPreviewShort() {
    NotesAppTheme {
        NoteCard(
            note = Note(
                title = "Sample Note",
                body = "This is the body of my sample note.",
                color = NoteColors.all[5]
            ),
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteCardPreviewLong() {
    NotesAppTheme {
        NoteCard(
            note = Note(
                title = "Sample Note With a Really Long Title That Should Be Truncated. " +
                        "Let's see how it looks when it exceeds the max lines limit.",
                body = "This is the body of my sample note. " +
                        "It can be quite long, but will be truncated in the preview." +
                        "\nlorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "\nlorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                        "\nlorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ",
                color = NoteColors.all[4]
            ),
            onClick = {},
        )
    }
}

// endregion
