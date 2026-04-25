package com.ansfartz.notesapp.data

/**
 * Sample notes used for seeding both the in-memory repository and the
 * Room database on first creation.  Defined once here so they can't
 * drift out of sync.
 */
fun createSampleNotes(): List<Note> = listOf(
    Note(
        id = 1L,
        title = "Grocery List",
        body = "Eggs\nMilk\nBread\nButter\nCheese",
        color = NoteColors.all[0],
    ),
    Note(
        id = 2L,
        title = "Meeting Notes",
        body = "Discuss Q2 roadmap\nReview design specs\nAssign sprint tasks",
        color = NoteColors.all[1],
    ),
    Note(
        id = 3L,
        title = "Ideas",
        body = "Build a notes app with Jetpack Compose",
        color = NoteColors.all[4],
    ),
    Note(
        id = 4L,
        title = "Workout Plan",
        body = "Monday: Chest & Triceps\nWednesday: Back & Biceps\nFriday: Legs & Shoulders",
        color = NoteColors.all[2],
    ),
    Note(
        id = 5L,
        title = "",
        body = "Remember to call Mom this weekend!",
        color = NoteColors.all[3],
    ),
)
