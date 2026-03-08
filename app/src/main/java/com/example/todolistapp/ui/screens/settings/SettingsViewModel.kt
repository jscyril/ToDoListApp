package com.example.todolistapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.TodoApp
import com.example.todolistapp.domain.repository.SettingsRepository
import com.example.todolistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val accentColorIndex: StateFlow<Int> = settingsRepository.getAccentColorIndex()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val defaultPriority: StateFlow<Int> = settingsRepository.getDefaultPriority()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val defaultCategoryId: StateFlow<Long> = settingsRepository.getDefaultCategoryId()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    fun setAccentColor(index: Int) {
        viewModelScope.launch { settingsRepository.setAccentColorIndex(index) }
    }

    fun setDefaultPriority(priority: Int) {
        viewModelScope.launch { settingsRepository.setDefaultPriority(priority) }
    }

    fun setDefaultCategoryId(id: Long) {
        viewModelScope.launch { settingsRepository.setDefaultCategoryId(id) }
    }

    fun deleteAllCompletedTasks() {
        viewModelScope.launch { taskRepository.deleteAllCompletedTasks() }
    }

    fun deleteAllTasks() {
        viewModelScope.launch { taskRepository.deleteAllTasks() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApp.instance
                return SettingsViewModel(app.settingsRepository, app.taskRepository) as T
            }
        }
    }
}
