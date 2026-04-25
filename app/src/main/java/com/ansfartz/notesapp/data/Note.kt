package com.ansfartz.notesapp.data

/**
 * Core data model — free of UI-framework types so it can be used by any
 * layer (data, domain, ViewModel) without pulling in Compose or Android UI dependencies.
 *
 * Color is stored as an ARGB [Int];the UI layer converts it
 * to [androidx.compose.ui.graphics.Color] when rendering.
 */
data class Note(
    val id: Long = System.nanoTime(),
    val title: String = "",
    val body: String = "",
    val color: Int = NoteColors.all.first(),
)

/**
 * Palette of ARGB color ints available for notes.
 * Stored as plain [Int] values so the data layer stays independent of Compose.
 * Convert in the UI layer with `Color(noteColor)`.
 */
object NoteColors {
    val all = listOf(
        0xFFFFF9C4.toInt(), // Yellow
        0xFFB2EBF2.toInt(), // Cyan
        0xFFC8E6C9.toInt(), // Green
        0xFFF8BBD0.toInt(), // Pink
        0xFFE1BEE7.toInt(), // Purple
        0xFFFFE0B2.toInt(), // Orange
        0xFFBBDEFB.toInt(), // Blue
        0xFFD7CCC8.toInt(), // Brown
    )
}
