package com.ansfartz.notesapp.data

import androidx.compose.ui.graphics.Color

data class Note(
    val id: Long = System.nanoTime(),
    val title: String = "",
    val body: String = "",
    val color: Color = NoteColors.all.first(),
)

object NoteColors {
    val all = listOf(
        Color(0xFFFFF9C4), // Yellow
        Color(0xFFB2EBF2), // Cyan
        Color(0xFFC8E6C9), // Green
        Color(0xFFF8BBD0), // Pink
        Color(0xFFE1BEE7), // Purple
        Color(0xFFFFE0B2), // Orange
        Color(0xFFBBDEFB), // Blue
        Color(0xFFD7CCC8), // Brown
    )
}
