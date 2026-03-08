package com.example.todolistapp.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.TodoApp
import com.example.todolistapp.domain.model.Category
import com.example.todolistapp.domain.repository.CategoryRepository
import com.example.todolistapp.domain.repository.TaskRepository
import com.example.todolistapp.util.DateUtils
import kotlinx.coroutines.flow.*

class StatisticsViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val totalTasks: StateFlow<Int> = taskRepository.getTotalTaskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTasks: StateFlow<Int> = taskRepository.getCompletedTaskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingTasks: StateFlow<Int> = taskRepository.getPendingTaskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overdueTasks: StateFlow<Int> = taskRepository.getOverdueTaskCount(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedThisWeek: StateFlow<Int> = taskRepository.getCompletedThisWeekCount(
        DateUtils.getStartOfWeek(), DateUtils.getEndOfWeek()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTaskCountForCategory(categoryId: Long): Flow<Int> =
        taskRepository.getTaskCountByCategory(categoryId)

    fun getTaskCountForPriority(priority: Int): Flow<Int> =
        taskRepository.getTaskCountByPriority(priority)

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApp.instance
                return StatisticsViewModel(app.taskRepository, app.categoryRepository) as T
            }
        }
    }
}
