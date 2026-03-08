package com.example.todolistapp.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Categories : Screen("categories")
    data object Statistics : Screen("statistics")
    data object Settings : Screen("settings")
    data object Onboarding : Screen("onboarding")
    data object AddEditTask : Screen("add_edit_task?taskId={taskId}") {
        fun createRoute(taskId: Long = -1L) = "add_edit_task?taskId=$taskId"
    }
    data object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
}

