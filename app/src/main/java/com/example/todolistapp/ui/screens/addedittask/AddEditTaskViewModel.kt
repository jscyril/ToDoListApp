package com.example.todolistapp.ui.screens.addedittask

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.*
import com.example.todolistapp.TodoApp
import com.example.todolistapp.domain.model.Category
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.domain.model.Task
import com.example.todolistapp.domain.repository.CategoryRepository
import com.example.todolistapp.domain.repository.TaskRepository
import com.example.todolistapp.notification.TaskReminderWorker
import com.example.todolistapp.util.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AddEditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val savedStateHandle: SavedStateHandle,
    private val context: Context
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>("taskId") ?: -1L
    val isEditing = taskId != -1L

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _priority = MutableStateFlow(Priority.LOW)
    val priority: StateFlow<Priority> = _priority

    private val _categoryId = MutableStateFlow(1L)
    val categoryId: StateFlow<Long> = _categoryId

    private val _dueDate = MutableStateFlow<Long?>(null)
    val dueDate: StateFlow<Long?> = _dueDate

    private val _dueTime = MutableStateFlow<Long?>(null)
    val dueTime: StateFlow<Long?> = _dueTime

    private val _titleError = MutableStateFlow(false)
    val titleError: StateFlow<Boolean> = _titleError

    private val _taskSaved = MutableStateFlow(false)
    val taskSaved: StateFlow<Boolean> = _taskSaved

    private var existingTask: Task? = null

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        if (isEditing) {
            viewModelScope.launch {
                taskRepository.getTaskById(taskId)?.let { task ->
                    existingTask = task
                    _title.value = task.title
                    _description.value = task.description
                    _priority.value = task.priority
                    _categoryId.value = task.categoryId
                    _dueDate.value = task.dueDate
                    _dueTime.value = task.dueTime
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _title.value = value
        if (value.isNotBlank()) _titleError.value = false
    }

    fun onDescriptionChange(value: String) { _description.value = value }
    fun onPriorityChange(value: Priority) { _priority.value = value }
    fun onCategoryChange(value: Long) { _categoryId.value = value }
    fun onDueDateChange(value: Long?) { _dueDate.value = value }
    fun onDueTimeChange(value: Long?) { _dueTime.value = value }

    fun saveTask() {
        if (_title.value.isBlank()) {
            _titleError.value = true
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (isEditing && existingTask != null) {
                val updated = existingTask!!.copy(
                    title = _title.value.trim(),
                    description = _description.value.trim(),
                    priority = _priority.value,
                    categoryId = _categoryId.value,
                    dueDate = _dueDate.value,
                    dueTime = _dueTime.value,
                    updatedAt = now
                )
                taskRepository.updateTask(updated)
                scheduleReminder(updated.id, updated.title, updated.dueDate, updated.dueTime)
            } else {
                val task = Task(
                    title = _title.value.trim(),
                    description = _description.value.trim(),
                    priority = _priority.value,
                    categoryId = _categoryId.value,
                    dueDate = _dueDate.value,
                    dueTime = _dueTime.value,
                    createdAt = now,
                    updatedAt = now
                )
                val id = taskRepository.insertTask(task)
                scheduleReminder(id, task.title, task.dueDate, task.dueTime)
            }
            _taskSaved.value = true
        }
    }

    private fun scheduleReminder(taskId: Long, title: String, dueDate: Long?, dueTime: Long?) {
        if (dueDate == null) return
        val triggerTime = if (dueTime != null) {
            DateUtils.combineDateAndTime(dueDate, dueTime)
        } else {
            dueDate
        }
        val delay = triggerTime - System.currentTimeMillis()
        if (delay <= 0) return

        val data = workDataOf(
            "task_id" to taskId,
            "task_title" to title
        )
        val request = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("task_reminder_$taskId")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("task_reminder_$taskId", ExistingWorkPolicy.REPLACE, request)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val app = TodoApp.instance
                val savedStateHandle = extras.createSavedStateHandle()
                return AddEditTaskViewModel(
                    app.taskRepository, app.categoryRepository, savedStateHandle, app
                ) as T
            }
        }
    }
}
