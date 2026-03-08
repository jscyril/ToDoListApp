package com.example.todolistapp.ui.screens.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.todolistapp.TodoApp
import com.example.todolistapp.domain.model.Category
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.domain.model.Task
import com.example.todolistapp.domain.repository.CategoryRepository
import com.example.todolistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L

    val task: StateFlow<Task?> = taskRepository.getTaskByIdFlow(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _category = MutableStateFlow<Category?>(null)
    val category: StateFlow<Category?> = _category

    private val _taskDeleted = MutableStateFlow(false)
    val taskDeleted: StateFlow<Boolean> = _taskDeleted

    init {
        viewModelScope.launch {
            task.collect { t ->
                if (t != null) {
                    _category.value = categoryRepository.getCategoryById(t.categoryId)
                }
            }
        }
    }

    fun toggleCompletion() {
        viewModelScope.launch {
            task.value?.let { t ->
                taskRepository.updateTask(
                    t.copy(isCompleted = !t.isCompleted, updatedAt = System.currentTimeMillis())
                )
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            task.value?.let { t ->
                taskRepository.deleteTask(t)
                _taskDeleted.value = true
            }
        }
    }

    fun changePriority(priority: Priority) {
        viewModelScope.launch {
            task.value?.let { t ->
                taskRepository.updateTask(
                    t.copy(priority = priority, updatedAt = System.currentTimeMillis())
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = TodoApp.instance
                val savedStateHandle = extras.createSavedStateHandle()
                return TaskDetailViewModel(
                    app.taskRepository, app.categoryRepository, savedStateHandle
                ) as T
            }
        }
    }
}
