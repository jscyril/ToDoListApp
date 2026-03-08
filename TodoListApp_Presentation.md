# ToDo List App — Android Project Documentation

### A Feature-Rich Task Manager Built with Kotlin & Jetpack Compose

> **Purpose of this document:** This markdown serves as both a slide deck script (for AI-generated PPT) and a 5–7 minute demonstration script. Each section maps to a presentation slide. Narration cues are written in _italics_.

---

## Slide 1 — Title Slide

# ToDo List App

**Platform:** Android | **Language:** Kotlin | **UI:** Jetpack Compose  
**Architecture:** MVVM + Clean Architecture | **Database:** Room (SQLite)  
**Version:** 1.0 | **Min SDK:** Android 8.0 (API 26) | **Target SDK:** API 34

_"Today I'll be walking you through the ToDo List App — a fully functional Android task management application I built using modern Android development practices. The app is written entirely in Kotlin and uses Jetpack Compose for its UI."_

---

## Slide 2 — App Overview & Goals

### What Does the App Do?

The ToDo List App is a **personal productivity tool** that lets users:

- Create, edit, and delete tasks
- Organize tasks into color-coded categories
- Set due dates and time-based reminders
- Filter, search, and sort their task list in real time
- Track their productivity through visual statistics
- Personalize the app's appearance with theme accent colors

### Who Is It For?

Students and professionals who need a clean, fast, offline-first task manager with no sign-up required.

_"The core goal was to build something that feels premium but works entirely offline — no backend, no account required. Everything is stored locally on the device."_

---

## Slide 3 — Technology Stack

| Layer               | Technology                     | Purpose                                |
| ------------------- | ------------------------------ | -------------------------------------- |
| **Language**        | Kotlin                         | Primary development language           |
| **UI Framework**    | Jetpack Compose                | Declarative, reactive UI               |
| **Architecture**    | MVVM + Clean Architecture      | Separation of concerns                 |
| **Local Database**  | Room (SQLite)                  | Persistent task & category storage     |
| **Async/Reactive**  | Kotlin Coroutines + Flow       | Non-blocking data streams              |
| **Preferences**     | DataStore (Preferences)        | App settings & onboarding state        |
| **Background Work** | WorkManager                    | Scheduling task reminder notifications |
| **Navigation**      | Navigation Compose             | Type-safe screen routing               |
| **Build Tool**      | KSP (Kotlin Symbol Processing) | Annotation processing for Room         |
| **Design System**   | Material Design 3              | UI components and theming              |

_"The stack is fully modern — everything here is Google's recommended approach for Android development in 2024. There's no legacy XML layouts; every single screen is built using Jetpack Compose."_

---

## Slide 4 — Architecture: Clean Architecture + MVVM

### Three-Layer Architecture

```
┌─────────────────────────────────────────────┐
│              UI Layer (Compose)              │
│   Screens + ViewModels + UI State           │
├─────────────────────────────────────────────┤
│              Domain Layer                    │
│   Models (Task, Category) + Repository      │
│   Interfaces + FilterType + SortOrder       │
├─────────────────────────────────────────────┤
│              Data Layer                      │
│   Room Database + DAOs + Entities           │
│   DataStore (Settings) + Repository Impl    │
└─────────────────────────────────────────────┘
```

### Key Architectural Decisions

- **Repository Pattern:** ViewModels never talk to the database directly — they go through a repository interface
- **Dependency Inversion:** Domain layer defines interfaces (`TaskRepository`, `CategoryRepository`, `SettingsRepository`); Data layer provides concrete implementations
- **Single Source of Truth:** All data flows reactively via Kotlin `Flow` from Room → Repository → ViewModel → UI

_"This is the textbook Clean Architecture pattern. The UI doesn't know anything about Room or SQLite — it just observes data from the ViewModel. If I ever wanted to swap the database for a remote API, I'd only need to change the data layer."_

---

## Slide 5 — Project File Structure

```
com.example.todolistapp/
├── MainActivity.kt              ← App entry point
├── TodoApp.kt                   ← Application class (DI/setup)
│
├── data/
│   ├── local/
│   │   ├── db/    TodoDatabase.kt       ← Room DB definition
│   │   ├── dao/   TaskDao.kt, CategoryDao.kt
│   │   ├── entity/ TaskEntity.kt, CategoryEntity.kt
│   │   └── datastore/ SettingsDataStore.kt
│   └── repository/
│       ├── TaskRepositoryImpl.kt
│       └── CategoryRepositoryImpl.kt
│
├── domain/
│   ├── model/     Task.kt, Category.kt, FilterType.kt, SortOrder.kt
│   └── repository/ TaskRepository.kt, CategoryRepository.kt, SettingsRepository.kt
│
├── navigation/    NavGraph.kt, Screen.kt
├── notification/  NotificationHelper.kt, TaskReminderWorker.kt
├── ui/
│   ├── components/  TaskCard.kt, EmptyState.kt, ConfirmDialog.kt
│   ├── screens/     home/, addedittask/, categories/, statistics/,
│   │                settings/, onboarding/, splash/, taskdetail/
│   └── theme/       Color.kt, Theme.kt, Type.kt
└── util/          DateUtils.kt, IconUtils.kt
```

_"The package structure directly mirrors the architecture. You can immediately tell what belongs where just from the folder name."_

---

## Slide 6 — Data Models

### Task Model

```kotlin
data class Task(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.LOW,   // LOW, MEDIUM, HIGH, URGENT
    val categoryId: Long = 1,
    val dueDate: Long? = null,               // epoch milliseconds
    val dueTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Priority Enum

```kotlin
enum class Priority(val level: Int, val label: String) {
    LOW(0, "Low"), MEDIUM(1, "Medium"), HIGH(2, "High"), URGENT(3, "Urgent")
}
```

### Category Model

```kotlin
data class Category(
    val id: Long = 0,
    val name: String = "",
    val iconName: String = "Category",   // maps to Material Icon
    val colorHex: String = "#00E5FF"     // hex color string
)
```

_"I separated the domain model from the database entity. The Task class is what the UI and ViewModels work with. The TaskEntity is what gets stored in SQLite — they're mapped in the repository."_

---

## Slide 7 — Database Layer: Room

### TodoDatabase Setup

```kotlin
@Database(
    entities = [TaskEntity::class, CategoryEntity::class],
    version = 1
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}
```

### Pre-populated Categories (on first launch)

The database seeds **6 default categories** automatically on first creation:

| Category | Icon              | Color         |
| -------- | ----------------- | ------------- |
| Personal | 👤 Person         | Electric Cyan |
| Work     | 💼 Work           | Soft Violet   |
| Shopping | 🛒 ShoppingCart   | Orange        |
| Health   | ❤️ FavoriteBorder | Neon Green    |
| Finance  | 🏦 AccountBalance | Amber Gold    |
| Ideas    | 💡 Lightbulb      | Red           |

### Foreign Key Integrity

`TaskEntity` has a foreign key to `CategoryEntity`. On category deletion, tasks are automatically moved to the default "Personal" category (`onDelete = ForeignKey.SET_DEFAULT`).

_"Room generates all the SQL under the hood. I never write raw SQL queries — I just define the DAO interface with annotations and Room generates the implementation at compile time using KSP."_

---

## Slide 8 — Data Access: TaskDao (Rich Query Interface)

The `TaskDao` provides **15+ queries** exposing data as reactive `Flow` streams:

```kotlin
@Dao
interface TaskDao {
    // Reactive streams (return Flow)
    fun getAllTasks(): Flow<List<TaskEntity>>
    fun searchTasks(query: String): Flow<List<TaskEntity>>
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>
    fun getActiveTasks(): Flow<List<TaskEntity>>
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>
    fun getOverdueTasks(now: Long): Flow<List<TaskEntity>>

    // Statistics aggregates
    fun getTotalTaskCount(): Flow<Int>
    fun getCompletedTaskCount(): Flow<Int>
    fun getPendingTaskCount(): Flow<Int>
    fun getOverdueTaskCount(now: Long): Flow<Int>
    fun getCompletedTodayCount(startOfDay: Long, endOfDay: Long): Flow<Int>
    fun getCompletedThisWeekCount(startOfWeek: Long, endOfWeek: Long): Flow<Int>

    // Mutations (suspend functions)
    suspend fun insertTask(task: TaskEntity): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteAllCompletedTasks()
}
```

_"Every query that returns data returns a Flow — meaning the UI automatically updates whenever the underlying data in the database changes. No manual refresh needed."_

---

## Slide 9 — App Entry: Application Class & MainActivity

### TodoApp.kt — Manual Dependency Injection

```kotlin
class TodoApp : Application() {
    lateinit var database: TodoDatabase
    lateinit var taskRepository: TaskRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, TodoDatabase::class.java, "todo_database")
            .addCallback(TodoDatabase.Callback { database.categoryDao() })
            .build()
        taskRepository = TaskRepositoryImpl(database.taskDao())
        categoryRepository = CategoryRepositoryImpl(database.categoryDao())
        settingsRepository = SettingsDataStore(this)
        createNotificationChannel()  // register notification channel at startup
    }
}
```

### MainActivity.kt — Reactive Theme & Navigation

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()         // full-screen edge-to-edge display
        requestNotificationPermission()  // runtime permission (Android 13+)
        setContent {
            val accentColorIndex by settingsDataStore.getAccentColorIndex()
                .collectAsStateWithLifecycle()
            ToDoListAppTheme(accentColorIndex = accentColorIndex) {
                NavGraph(showOnboarding = ..., accentColorIndex = ...)
            }
        }
    }
}
```

_"The Application class is where all the object creation happens — it acts like a very simple dependency injector. The ViewModels pull their repositories from TodoApp.instance."_

---

## Slide 10 — Navigation Architecture

### Screen Routes

```kotlin
sealed class Screen(val route: String) {
    object Splash     : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home       : Screen("home")
    object Categories : Screen("categories")
    object Statistics : Screen("statistics")
    object Settings   : Screen("settings")
    object AddEditTask: Screen("add_edit_task?taskId={taskId}")
    object TaskDetail : Screen("task_detail/{taskId}")
}
```

### Navigation Flow

```
App Launch
    └─► Splash Screen (1.5s animated logo)
            ├─► Onboarding (first launch only, 3-page HorizontalPager)
            └─► Home Screen ──────────────────────┐
                    │                              │
                    ├── Bottom Nav ──► Categories  │
                    │               ──► Statistics │
                    │               ──► Settings   │
                    │                              │
                    ├── FAB ──► Add Task Screen    │
                    └── Tap Task ──► Task Detail ──┘
                                      └── Edit ──► Edit Task Screen
```

- **Bottom Navigation Bar** shows on main tabs (Home, Categories, Statistics, Settings)
- **Animated Transitions:** Fade in/out with 300ms tween across all screen transitions
- Single `NavController` manages the entire back stack

_"I used Compose Navigation which is type-safe and integrates perfectly with Compose's state system. The bottom nav bar automatically hides when you're on a detail screen."_

---

## Slide 11 — Home Screen: The Core Feature

### What's on the Home Screen

1. **Dynamic greeting** — "Good Morning / Afternoon / Evening" based on time of day
2. **Today's date** displayed below the greeting
3. **Stats Bar** — 4 live-updating chips showing: Total / Done Today / Pending / Overdue
4. **Search Bar** — animates in/out with expand/collapse animation
5. **Filter Chips** — All, Today, Upcoming, Overdue, Completed, + all user categories
6. **Sort Options** — Date Created / Due Date / Priority / Alphabetical (bottom sheet)
7. **Task List** — with staggered entrance animations per task card
8. **Swipe-to-Delete** with undo Snackbar (5-second undo window)
9. **FAB** — Spring-animated floating action button to add new tasks

### HomeViewModel: Reactive Data Pipeline

The ViewModel uses `combine()` to merge 5 reactive flows:

```kotlin
val tasks: StateFlow<List<Task>> = combine(
    _searchQuery, _filterType, _filterCategoryId, _sortOrder, allTasks
) { query, filter, categoryId, sort, taskList ->
    // 1. Apply search filter
    // 2. Apply date/category/status filter
    // 3. Apply sort order
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
```

_"The home screen is the heart of the app. The ViewModel recomputes the filtered and sorted task list any time the search query, filter choice, sort order, or the underlying database changes — all reactively with no manual refreshes."_

---

## Slide 12 — Task Card & Swipe-to-Delete

### TaskCard Features

- Checkbox to toggle completion
- Task title in bold
- Priority indicator dot (color-coded: grey / yellow / orange / red)
- Category chip with icon + color
- Due date badge (red if overdue, orange if due today)

### Swipe-to-Delete with Undo

```kotlin
SwipeToDismissBox(
    enableDismissFromEndToStart = true,  // right-to-left swipe only
    backgroundContent = {
        // Red delete background with trash icon
    }
) {
    TaskCard(...)
}

// After delete, show undo snackbar
val result = snackbarHostState.showSnackbar(
    message = "Task deleted",
    actionLabel = "UNDO",
    duration = SnackbarDuration.Short
)
if (result == SnackbarResult.ActionPerformed) {
    viewModel.insertTask(deletedTask)  // restore the task
}
```

_"One of my favorite UX details is the swipe-to-delete with undo. If you accidentally swipe away a task, you get a Snackbar with an UNDO button. The task is held in memory and re-inserted if needed."_

---

## Slide 13 — Add / Edit Task Screen

### Form Fields

- **Title** (required, with validation error message)
- **Description** (optional, multi-line)
- **Priority** — 4 filter chips: Low / Medium / High / Urgent
- **Category** — horizontal scrollable chip row with icon + color per category
- **Due Date** — Material 3 `DatePickerDialog`
- **Due Time** — Material 3 `TimePickerDialog` (clock face UI)
- **Clear Date & Time** — conditional button to remove the schedule

### Save Logic

- Validation: title must be non-empty
- On save: if a due date+time has been set, a **WorkManager reminder** is scheduled
- If editing an existing task: cancels the old WorkManager job and creates a new one
- After save: navigates back automatically

_"The same screen handles both creating new tasks and editing existing ones. The ViewModel knows if it received a taskId in the navigation arguments — if yes, it loads the existing task; if not, it starts a fresh blank form."_

---

## Slide 14 — Notifications & WorkManager

### Architecture

```
User sets due date/time on a task
        │
        ▼
AddEditTaskViewModel.saveTask()
        │
        ▼
WorkManager.setExactAndAllowWhileIdle()
  schedules TaskReminderWorker at due time
        │
        ▼
At due time, OS triggers TaskReminderWorker
        │
        ▼
NotificationHelper.showTaskReminder()
  delivers high-priority notification
  tapping opens the app
```

### Notification Channel

Created at app startup in `TodoApp.onCreate()`:

- **Channel ID:** `task_reminders`
- **Importance:** HIGH (shows as heads-up notification)
- **Auto-cancel:** yes (dismissed on tap)

### Android 13+ Permission

`MainActivity` requests `POST_NOTIFICATIONS` permission at launch using the Activity Result API.

_"WorkManager is the right tool for this because it survives device restarts and app kills — Android guarantees the work will run even if the user closes the app, as long as the device is on."_

---

## Slide 15 — Categories Screen

### Features

- View all categories in a grid or list
- Create new custom categories with:
  - Custom name
  - Icon picker (from Material Icons library)
  - Color picker
- Edit existing categories
- Delete a category (tasks are safely reassigned to "Personal")
- Each category card shows the task count badge

### CategoriesViewModel

- Observes `categoryRepository.getAllCategories()` as a `StateFlow`
- For each category, dynamically queries task count via `taskRepository.getTaskCountByCategory(id)`

_"Categories are fully customizable. The 6 defaults are just a starting point — users can create their own with any icon from the Material icon set and a custom hex color."_

---

## Slide 16 — Statistics Screen

### Visualizations

1. **Completion Rate Ring Chart** — animated circular arc drawn with Compose `Canvas` API
   - Smoothly animates from 0% to the actual percentage on load
2. **Overview Stat Cards** — Total, Done, Pending, Overdue (counts animate up with `animateIntAsState`)

3. **Completed This Week** — trending up badge with count

4. **Tasks by Category** — animated horizontal progress bars per category

5. **Tasks by Priority** — animated horizontal progress bars (Low / Medium / High / Urgent)

### The Canvas Ring Chart

```kotlin
Canvas(modifier = Modifier.size(160.dp)) {
    // Background ring (grey)
    drawCircle(color = DarkGrey, style = Stroke(...))
    // Foreground arc (animated, colored)
    drawArc(
        color = ElectricCyan,
        startAngle = -90f,
        sweepAngle = animatedProgress * 360f,
        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
    )
}
```

_"I used the Compose Canvas API to draw the ring chart — no external charting library. The sweep angle is driven by an `animateFloatAsState`, so it smoothly animates in when the screen loads."_

---

## Slide 17 — Settings Screen

### Customization Options

#### Appearance

- **Accent Color Picker:** 6 colors to choose from
  - Electric Cyan (#00E5FF)
  - Soft Violet (#BB86FC)
  - Neon Green (#30D158)
  - Coral Pink (#FF6B6B)
  - Amber Gold (#FFD600)
  - Sky Blue (#64B5F6)

#### Defaults

- **Default Priority:** sets the pre-selected priority when creating a new task

#### Danger Zone

- **Delete All Completed Tasks** (with confirmation dialog)
- **Delete ALL Tasks** (with confirmation dialog)

#### About

- App name and version (1.0)

### Persistence via DataStore

Settings are stored using Jetpack DataStore (Preferences API) — a modern, type-safe replacement for SharedPreferences:

```kotlin
val ACCENT_COLOR_INDEX = intPreferencesKey("accent_color_index")
val DEFAULT_PRIORITY = intPreferencesKey("default_priority")
val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
```

_"The accent color change is applied globally and immediately — because the color is read as a Flow in MainActivity and the entire Compose theme re-renders reactively."_

---

## Slide 18 — Onboarding Flow

### 3-Page Horizontal Pager (shown only on first launch)

| Page | Icon            | Title                    | Message                                        |
| ---- | --------------- | ------------------------ | ---------------------------------------------- |
| 1    | ✅ TaskAlt      | Welcome to ToDo ✨       | Your beautiful, minimal task manager           |
| 2    | 📂 Category     | Organize with Categories | Create custom categories with colors and icons |
| 3    | 🚀 RocketLaunch | Let's Get Started!       | Stay productive, stay organized                |

### UX Details

- **Animated dots indicator** — active dot expands to 24dp width using spring animation
- **Pulsing icon** on each page (infinite scale animation between 0.95x and 1.05x)
- **Skip button** — jumps directly to the app
- **Next / Get Started button** — advances pages
- Onboarding completion flag stored in DataStore; never shown again

_"The onboarding is skippable at any point. Once completed or skipped, DataStore saves the flag and the user goes straight to the home screen on every future launch."_

---

## Slide 19 — Dark AMOLED Theme System

### Design Philosophy

The app uses a **pure AMOLED black theme** — true black (`#000000`) backgrounds to save battery on OLED screens.

### Color Palette

| Role           | Color       | Hex       |
| -------------- | ----------- | --------- |
| Background     | Black       | `#000000` |
| Card Surface   | Dark Card   | `#121212` |
| Borders        | Dark Border | `#1C1C1E` |
| Primary Text   | White       | `#FFFFFF` |
| Secondary Text | Grey        | `#8E8E93` |
| Success        | Neon Green  | `#30D158` |
| Warning        | Orange      | `#FF9F0A` |
| Danger         | Red         | `#FF453A` |

### Dynamic Accent Color

```kotlin
// Theme.kt — compositionLocalOf makes the accent available anywhere in the tree
val LocalAccentColor = compositionLocalOf { ElectricCyan }

@Composable
fun ToDoListAppTheme(accentColorIndex: Int, content: @Composable () -> Unit) {
    val accentColor = AccentColors[accentColorIndex]
    CompositionLocalProvider(LocalAccentColor provides accentColor) {
        MaterialTheme(colorScheme = darkColorScheme(...), content = content)
    }
}
```

_"The accent color system is elegant — I store the index in DataStore, read it in MainActivity, pass it to the theme wrapper, and every composable in the tree that uses `LocalAccentColor.current` automatically picks up the right color."_

---

## Slide 20 — Key Technical Highlights

### 1. Reactive Data Pipeline

`Room → Flow → Repository → ViewModel.stateIn() → Compose collectAsStateWithLifecycle()`  
Data changes propagate automatically end-to-end with zero manual refresh code.

### 2. Compose + ViewModel Integration

Each screen gets its own ViewModel via `viewModel(factory = ...)`. The ViewModel survives configuration changes (screen rotation) while the Composable re-renders cleanly.

### 3. Staggered List Animation

```kotlin
LaunchedEffect(Unit) {
    delay(index * 50L)  // each card enters 50ms after the previous
    visible = true
}
```

### 4. Edge-to-Edge UI

`enableEdgeToEdge()` in `MainActivity` + proper `contentPadding` handling makes the app draw behind the status bar and navigation bar for a truly immersive look.

### 5. Type-Safe Navigation

Navigation arguments are declared with `NavType` — accessing a task by ID in `TaskDetailScreen` is type-safe, not string-based.

### 6. No Third-Party Libraries (except Kotlin/Jetpack)

The entire UI, animations, and database are built using only Google's official libraries. No third-party charting libs, no Hilt, no Retrofit — keeping the dependency tree minimal.

_"From an Android engineering perspective, the reactive pipeline is what I'm most proud of. The ViewModel's tasks StateFlow is the output of combining 5 input flows — every filter, sort, and search change triggers an automatic recomputation."_

---

## Slide 21 — Demo Walkthrough (Live Demonstration Script)

**[Step 1: Launch the app]**
_"On first launch, you see the Splash screen — an animated logo — followed by the 3-page Onboarding flow. I'll skip that since I've already completed it."_

**[Step 2: Home Screen]**
_"Here's the Home screen. Notice the personalized greeting, today's date, and the 4 summary stats at the top."_

**[Step 3: Add a task]**
_"I'll tap the FAB to create a new task. I'll give it a title, set it to High priority, assign it to the Work category, and set a due date for today."_

**[Step 4: Task appears with animation]**
_"The task slides in from below with a staggered animation. Notice the color-coded priority dot and the category chip."_

**[Step 5: Filter tasks]**
_"I can filter by Today — only tasks due today appear. Or I can filter by a specific category like Work."_

**[Step 6: Search]**
_"The search bar animates in from the top. I'll type a keyword and the list filters in real time."_

**[Step 7: Swipe to delete + undo]**
_"Let me swipe a task to delete it — watch the red background reveal. And here's the Undo snackbar."_

**[Step 8: Statistics]**
_"The Stats tab shows a live completion ring chart and animated progress bars by category and priority."_

**[Step 9: Settings — accent color]**
_"In Settings, I can change the accent color. Watch how the entire app theme updates instantly when I tap a different color."_

**[Step 10: Categories]**
_"Finally, the Categories screen shows all my categories with their task counts."_

---

## Slide 22 — Challenges & Learnings

### Challenges Faced

| Challenge                                       | Solution                                                               |
| ----------------------------------------------- | ---------------------------------------------------------------------- |
| Reactive filtering across multiple flows        | Used `combine()` to merge 5 StateFlows into one derived StateFlow      |
| Theme accent color applied globally             | Used `CompositionLocalProvider` with `LocalAccentColor`                |
| Swipe-to-delete without losing task for undo    | Held deleted task in a local variable before ViewModel delete          |
| Custom ring chart without a library             | Used Compose `Canvas` API with `drawArc` and `animateFloatAsState`     |
| Seeding default categories on first DB creation | Used `RoomDatabase.Callback.onCreate()` to insert seeds asynchronously |
| Foreign key constraint on category deletion     | Used `ForeignKey.SET_DEFAULT` to reassign orphaned tasks               |

### Key Learnings

- Clean Architecture makes the codebase maintainable and testable by design
- `StateFlow` + `collectAsStateWithLifecycle()` is the correct Compose + lifecycle-aware pattern
- DataStore is a strictly better drop-in for SharedPreferences
- WorkManager is the right choice for reliability-critical background tasks (notifications)
- Compose Canvas API is powerful enough to replace most charting libraries for simple use cases

---

## Slide 23 — Summary

### What Was Built

✅ Full CRUD task management with priorities, categories, and due dates  
✅ Reactive real-time filtering, searching, and sorting  
✅ Offline-first — 100% local SQLite storage via Room  
✅ Push notifications via WorkManager  
✅ Live productivity statistics with custom animations  
✅ Fully customizable AMOLED dark theme  
✅ Clean Architecture + MVVM — scalable and testable  
✅ First-run onboarding experience  
✅ Swipe-to-delete with undo  
✅ Edge-to-edge immersive UI

### Lines of Code

- **44 Kotlin source files**
- Screens: 8 full screens + 1 splash
- ViewModels: 6 ViewModels
- Database: 2 tables, 15+ DAO queries
- Animations: staggered lists, ring chart, filter chip transitions, FAB spring, onboarding pulse

_"This project gave me hands-on experience with modern Android development from the database all the way up to the UI — following the same architecture patterns used in production apps at Google and top Android studios."_

---

## Slide 24 — Thank You

# Thank You

**ToDo List App** — Built with Kotlin + Jetpack Compose  
Architecture: Clean Architecture + MVVM  
Storage: Room DB + DataStore  
Background: WorkManager

_Questions?_

---

> **Document prepared for:** AI PPT generation + 5–7 minute video demonstration script  
> **Project location:** `/mnt/data/college/3_tri/android_dev/ToDoListApp/`
