# NotesApp — MVVM Architecture Guide

A simple note-taking Android app built with Jetpack Compose, designed to
demonstrate **Model-View-ViewModel (MVVM)** architecture with clear layer
separation.

---

## Project Structure

```
com.ansfartz.notesapp/
├── MainActivity.kt                        ← App entry point
├── NotesApplication.kt                    ← Dependency container (provides repo)
├── data/                                  ← MODEL layer
│   ├── Note.kt                            ← Data model + color palette
│   ├── local/
│   │   ├── NoteEntity.kt                  ← Room entity + mappers
│   │   ├── NoteDao.kt                     ← Data access object (SQL queries)
│   │   └── NotesDatabase.kt               ← Room database + seed callback
│   └── repository/
│       ├── NoteRepository.kt              ← Interface (contract)
│       ├── InMemoryNoteRepository.kt      ← In-memory implementation
│       └── LocalNoteRepository.kt         ← Room/SQLite implementation
├── ui/                                    ← VIEW layer
│   ├── navigation/
│   │   ├── NotesNavGraph.kt               ← Wires screens to ViewModel
│   │   └── Screen.kt                      ← Route definitions
│   ├── notes/
│   │   ├── NotesScreen.kt                 ← Note list (staggered grid)
│   │   └── NoteEditScreen.kt              ← Add/edit note screen
│   └── theme/                             ← Material 3 theming
└── viewmodel/                             ← VIEWMODEL layer
    ├── NotesUiState.kt                    ← UI state data class
    ├── NotesViewModel.kt                  ← Bridges data ↔ UI
    └── NotesViewModelFactory.kt           ← Injects repository into ViewModel
```

Each top-level package maps to an MVVM layer, making the architecture
visible at a glance.

---

## The Three Layers Explained

### 1. Model — `data/`

**What goes here:** Data classes, data-access logic (repositories), and
anything related to *where data comes from or how it's stored*.

**Why it matters:** The Model layer has **zero knowledge of the UI**.  It
doesn't import Compose, Android Views, or any presentation code.  This
means you could swap Compose for a different UI toolkit and the data layer
wouldn't change at all.

#### `Note.kt` — The data model

```kotlin
data class Note(
    val id: Long = System.nanoTime(),
    val title: String = "",
    val body: String = "",
    val color: Int = NoteColors.all.first(),   // ARGB int, NOT Compose Color
)
```

#### `NoteRepository.kt` — Interface (contract)

```kotlin
interface NoteRepository {
    val notes: Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(id: Long)
}
```

**Why an interface?**  
The ViewModel depends on the *contract*, not a concrete class.  This
means you can swap implementations without changing any ViewModel or UI code:

| Implementation | Backing store | When to use |
|----------------|---------------|-------------|
| `InMemoryNoteRepository` | `MutableStateFlow` in process memory | Prototyping, unit tests |
| `LocalNoteRepository` | Room/SQLite on disk | Production (default) |
| *(future)* `RemoteNoteRepository` | REST API / Firebase | Cloud sync |

To swap, change one line in `NotesApplication.kt`:

```kotlin
val noteRepository: NoteRepository by lazy {
    LocalNoteRepository(database.noteDao())   // ← persisted (default)
    // InMemoryNoteRepository()               // ← in-memory alternative
}
```

**Why `Flow` instead of `StateFlow`?**  
Room's DAO returns a `Flow` backed by SQLite triggers.  `StateFlow` is a
subtype of `Flow`, so the in-memory implementation can still use
`MutableStateFlow` under the hood.  The ViewModel converts the `Flow` to
`StateFlow` via `stateIn()` — that's the ViewModel's job, not the
Repository's.

**Why `suspend`?**  
Database operations can't run on the main thread (they'd freeze the UI).
`suspend` lets Room run them on a background thread automatically.
The in-memory implementation's `suspend` methods complete instantly —
no actual suspension happens, so no performance penalty.

#### `LocalNoteRepository.kt` — Room/SQLite implementation

```kotlin
class LocalNoteRepository(private val dao: NoteDao) : NoteRepository {
    override val notes: Flow<List<Note>> =
        dao.observeAll().map { entities -> entities.map { it.toNote() } }

    override suspend fun saveNote(note: Note) = dao.upsert(note.toEntity())
    override suspend fun deleteNote(id: Long) = dao.deleteById(id)
}
```

**Why a separate `NoteEntity`?**  

The Room entity (`NoteEntity`) has database annotations (`@Entity`,`@PrimaryKey`).  
The domain model (`Note`) stays clean.  
Mapper extension functions are defined alongside `NoteEntity` and invoked exclusively in the repository layer to convert between the two.  
This means the database schema never leaks into the UI or ViewModel.

**Why a Repository?**  
The Repository is the **single source of truth** for note data.  The
ViewModel never manages the data directly — it asks the Repository.  This
has several benefits:

- **Testability:** You can test data logic independently of the ViewModel.
- **Replaceability:** Swap in-memory storage for Room, a REST API, or
  Firebase without touching the ViewModel or UI.
- **Reactive updates:** The Repository exposes a `Flow`.  When data
  changes, every observer (ViewModel, and in turn the UI) is notified
  automatically — no manual "refresh" calls needed.

---

### 2. View — `ui/`

**What goes here:** Composables (screens, components), navigation, and
theming.  Everything the user *sees and interacts with*.

**Why it matters:** Views are **stateless** — they receive data and
callbacks as parameters, and render accordingly.  They never create or
mutate data on their own.

#### Stateless composables

```kotlin
@Composable
fun NotesScreen(
    notes: List<Note>,        // Data flows IN
    onNoteClick: (Long) -> Unit,  // Events flow OUT
    onAddNote: () -> Unit,
    modifier: Modifier = Modifier,
)
```

**Why stateless?**

- **Previewable:** You can render any screen in `@Preview` by passing
  fake data — no ViewModel or database needed.
- **Testable:** UI tests supply data directly; no need to mock a ViewModel.
- **Reusable:** The same screen composable works in different navigation
  contexts.

The View doesn't know *where* data comes from or *what happens* when the
user taps a button.  It just calls the callback and trusts that the layer
above (the nav graph / ViewModel) handles it.

#### Navigation (`NotesNavGraph.kt`)

The nav graph is the **wiring layer** — it connects Views to the
ViewModel:

```kotlin
val app = LocalContext.current.applicationContext as NotesApplication
val viewModel: NotesViewModel = viewModel(
    factory = NotesViewModelFactory(app.noteRepository),
)
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

NotesScreen(
    notes = uiState.notes,
    onNoteClick = { navController.navigate(...) },
    onAddNote = { navController.navigate(...) },
)
```

It obtains the repository from the Application class (acting as a simple
dependency container), creates the ViewModel via a factory, collects the
`StateFlow`, passes data down to screens, and routes user actions
(navigation events, save/delete) to the right place.

---

### 3. ViewModel — `viewmodel/`

**What goes here:** ViewModels and UI state classes.  The ViewModel
*transforms* data-layer state into UI-ready state and exposes it as a
reactive stream.

**Why it matters:** The ViewModel is the **bridge** between the Model and
the View.  It survives configuration changes (screen rotation) and
contains no reference to Activities, Fragments, or Composables.

#### `NotesUiState.kt` — UI state container

```kotlin
data class NotesUiState(
    val notes: List<Note> = emptyList(),
)
```

**Why wrap in a UiState class?**  
Today it holds only a list.  In a real app, you'd add fields like:

```kotlin
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
)
```

A single state class means the UI only observes **one stream**, which
simplifies the View and prevents inconsistent state (e.g., showing both
a loading spinner and an error at the same time).

#### `NotesViewModel.kt` — State transformer

```kotlin
class NotesViewModel(
    private val repository: NoteRepository = InMemoryNoteRepository(),
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = repository.notes
        .map { notes -> NotesUiState(notes = notes) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, NotesUiState())

    fun getNoteById(id: Long): Note? =
        uiState.value.notes.find { it.id == id }

    fun saveNote(note: Note) {
        viewModelScope.launch { repository.saveNote(note) }
    }
}
```

**Key design decisions:**

- **Delegates to Repository:** `saveNote()` and `deleteNote()` launch
  a coroutine on `viewModelScope` that calls the `suspend` repository
  method.  The ViewModel doesn't duplicate data logic.
- **`getNoteById` reads from local state:** Instead of hitting the
  repository (which would require `suspend`), it reads from the already-
  collected `uiState`.  By the time the user navigates to the edit
  screen, the notes are already loaded.
- **Reactive chain:** `repository.notes` → `map` → `stateIn`.  When the
  Repository's data changes, the ViewModel's `uiState` updates
  automatically.  No manual refresh.
- **`stateIn` with `Eagerly`:** The flow starts collecting from the
  moment the ViewModel is created, so `uiState.value` is always up to
  date.  This is simpler to test and reason about.  In larger apps you
  might switch to `WhileSubscribed(5_000)` to stop collecting when no
  UI is observing — but that requires active collectors in tests rather
  than simple `.value` reads.
- **`initialValue`:** Set to an empty `NotesUiState()`.  The data loads
  almost instantly from Room (or immediately from in-memory), so the
  empty state is never visible to the user.

---

## Data Flow

Following the layered architecture described in Manuel Vicente Vivo's
*high-level client architecture* diagrams, the app is split into a
**UI Layer** and a **Data Layer**, connected through dependency injection:

```
Client
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│  ┌─ UI Layer ────────────────────────────────────────────────┐  │
│  │                                                           │  │
│  │  ┌═════════════════════════════════════════════════════┐  │  │
│  │  │                     Navigator                       │  │  │
│  │  │                  (NotesNavGraph)                    │  │  │
│  │  └════╤════════════════════════════════╤═══════════════┘  │  │
│  │       │                                │                  │  │
│  │       ▼                                ▼                  │  │
│  │  ┌──────────────┐          ┌─────────────────┐            │  │
│  │  │ NotesScreen  │          │ NoteEditScreen  │            │  │
│  │  └──────────────┘          └─────────────────┘            │  │
│  │         ▲                           ▲                     │  │
│  │         │        state / events     │                     │  │
│  │  ┌──────┴───────────────────────────┴─────────────┐       │  │
│  │  │                  State Holder                  │       │  │
│  │  │                (NotesViewModel)                │       │  │
│  │  └────────────────────┬───────────────────────────┘       │  │
│  │                       │                                   │  │
│  └───────────────────────┼───────────────────────────────────┘  │
│                          │                                      │
│  ╔═══════════════════════╧════════════════════════════════════╗ │
│  ║          Manual DI  (NotesApplication)                     ║ │
│  ╚═══════════════════════╤════════════════════════════════════╝ │
│                          │                                      │
│  ┌─ Data Layer ──────────┼───────────────────────────────────┐  │
│  │                       │                                   │  │
│  │                       ▼                                   │  │
│  │            ┌────────────────────┐                         │  │
│  │            │  NoteRepository    │  (interface)            │  │
│  │            │  notes: Flow       │                         │  │
│  │            └─────────┬──────────┘                         │  │
│  │            ┌─────────┴─────────┐                          │  │
│  │            │                   │                          │  │
│  │            ▼                   ▼                          │  │
│  │   ┌──────────────┐        ┌──────────────┐                │  │
│  │   │    Local     │        │  In-Memory   │                │  │
│  │   │  Repository  │        │  Repository  │                │  │
│  │   └──────┬───────┘        └──────────────┘                │  │
│  │          │                                                │  │
│  │          ▼                                                │  │
│  │   ┌──────────────┐                                        │  │
│  │   │   NoteDao    │                                        │  │
│  │   └──────┬───────┘                                        │  │
│  │          │                                                │  │
│  │          ▼                                                │  │
│  │     (notes.db)                                            │  │
│  │                                                           │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

**Unidirectional data flow (UDF):**

- **State flows up:** Data sources → Repository (`Flow`) → ViewModel
  (`stateIn`) → Screens (`collectAsStateWithLifecycle`).  The UI
  observes a stream and recomposes automatically.
- **Events flow down:** Screens → ViewModel (`saveNote()`, `deleteNote()`)
  → Repository → Data source (`suspend` write to Room or in-memory).

The **Manual DI** bar represents `NotesApplication`, which acts as a
simple service locator — it creates the Room database and repository as
lazy singletons and exposes them for `NotesViewModelFactory` to inject
into the ViewModel.  With Hilt, this manual wiring would be replaced by
`@HiltViewModel` + `@Inject` annotations.

---

## Testing Strategy

The layer separation enables focused, fast tests:

| Layer | Test type | What's tested | Location |
|-------|-----------|---------------|----------|
| Model | Unit test | Repository CRUD, reactivity | `test/.../data/repository/` |
| ViewModel | Unit test | State mapping, delegation | `test/.../viewmodel/` |
| View | Instrumented | Composable rendering, clicks | `androidTest/.../ui/notes/` |
| Navigation | Instrumented | Screen transitions, E2E | `androidTest/.../ui/navigation/` |

**`MainDispatcherRule`:** ViewModel tests need this JUnit rule because
`viewModelScope` uses `Dispatchers.Main`, which doesn't exist in a plain
JVM test.  The rule swaps in an `UnconfinedTestDispatcher` so coroutines
execute immediately.

---

## Why Not Other Patterns?

| Pattern | Difference from MVVM |
|---------|---------------------|
| **MVC** | Controller and View are often tightly coupled.  Hard to test. |
| **MVP** | Presenter holds a reference to the View interface — more boilerplate, harder to use with Compose. |
| **MVI** | Like MVVM but with explicit `Intent` objects for every user action and a `reduce` function.  More predictable, more verbose.  Natural next step from this codebase. |

---

## Natural Next Steps

These improvements build on the current MVVM foundation:

1. **Persistence (Room)** ✅ **Done!** `LocalNoteRepository` persists
   notes to SQLite via Room.  Swappable with `InMemoryNoteRepository`
   by changing one line in `NotesApplication.kt`.

2. **Dependency Injection (Hilt):** Replace `NotesApplication` manual DI
   and `NotesViewModelFactory` with `@HiltViewModel` + `@Inject`.
   Cleaner wiring, automatic scoping.

3. **Repository Interface** ✅ **Done!** `NoteRepository` is an
   interface with two implementations: `InMemoryNoteRepository` and
   `LocalNoteRepository`.

4. **MVI Refactor:** Introduce a sealed `NotesIntent` class
   (`AddNote`, `DeleteNote`, `UpdateNote`) and a `reduce()` function in
   the ViewModel.  Gives you a complete audit trail of every state change.

5. **Domain Layer (Use Cases):** For larger apps, add a `domain/` package
   with use-case classes like `SaveNoteUseCase` that encapsulate business
   rules.  Keeps the ViewModel thin.

6. **Remote Repository:** Add a `RemoteNoteRepository` backed by a REST
   API, or combine local + remote in an `OfflineFirstNoteRepository` that
   reads from Room and syncs with the server.

---

## Build & Run

```sh
./gradlew assembleDebug          # Build debug APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (emulator required)
```
