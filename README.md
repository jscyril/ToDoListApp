# ToDoList App

A feature-rich, offline-first task manager for Android built with **Kotlin** and **Jetpack Compose**.

## Features

- ✅ Create, edit, and delete tasks with title, description, priority, and category
- 📂 Organize tasks into color-coded, icon-tagged categories
- 📅 Set due dates and times with WorkManager-powered reminders
- 🔍 Real-time search, filtering (Today / Upcoming / Overdue), and sorting
- 📊 Statistics screen with animated completion ring chart and progress bars
- 🎨 6 selectable accent colors with an AMOLED dark theme
- ↩️ Swipe-to-delete with undo via Snackbar
- 🚀 3-page onboarding flow (shown once on first launch)

## Tech Stack

| Layer        | Technology                          |
| ------------ | ----------------------------------- |
| Language     | Kotlin                              |
| UI           | Jetpack Compose + Material Design 3 |
| Architecture | MVVM + Clean Architecture           |
| Database     | Room (SQLite)                       |
| Async        | Kotlin Coroutines + Flow            |
| Preferences  | DataStore                           |
| Background   | WorkManager                         |
| Navigation   | Navigation Compose                  |
| Build        | KSP                                 |

## Architecture

The app follows Clean Architecture with three layers:

```text
UI Layer      →  Screens + ViewModels (Compose)
Domain Layer  →  Models (Task, Category) + Repository Interfaces
Data Layer    →  Room Database + DAOs + DataStore
```

## Screenshots

> Coming soon

## Getting Started

1. Clone the repo
   ```bash
   git clone https://github.com/jscyril/ToDoListApp.git
   ```
2. Open in **Android Studio Hedgehog** or newer
3. Build and run on a device or emulator (min SDK 26 / Android 8.0)

> **Note:** No API keys or backend setup required — fully offline.

## License

For academic purposes.
