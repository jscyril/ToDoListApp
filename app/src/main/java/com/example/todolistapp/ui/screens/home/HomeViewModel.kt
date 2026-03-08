package com.example.todolistapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.TodoApp
import com.example.todolistapp.domain.model.*
import com.example.todolistapp.domain.repository.CategoryRepository
import com.example.todolistapp.domain.repository.TaskRepository
import com.example.todolistapp.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType

    private val _filterCategoryId = MutableStateFlow<Long?>(null)
    val filterCategoryId: StateFlow<Long?> = _filterCategoryId

    private val _sortOrder = MutableStateFlow(SortOrder.DATE_CREATED)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allTasks: Flow<List<Task>> = taskRepository.getAllTasks()

    val tasks: StateFlow<List<Task>> = combine(
        _searchQuery,
        _filterType,
        _filterCategoryId,
        _sortOrder,
        allTasks
    ) { query, filter, categoryId, sort, taskList ->
        var result = if (query.isNotBlank()) {
            taskList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        } else {
            taskList
        }

        result = when (filter) {
            FilterType.ALL -> result
            FilterType.TODAY -> result.filter { DateUtils.isToday(it.dueDate) }
            FilterType.UPCOMING -> result.filter {
                it.dueDate != null && it.dueDate > System.currentTimeMillis() && !it.isCompleted
            }
            FilterType.OVERDUE -> result.filter {
                DateUtils.isOverdue(it.dueDate, it.dueTime) && !it.isCompleted
            }
            FilterType.COMPLETED -> result.filter { it.isCompleted }
            FilterType.BY_CATEGORY -> {
                if (categoryId != null) result.filter { it.categoryId == categoryId }
                else result
            }
        }

        when (sort) {
            SortOrder.DATE_CREATED -> result.sortedWith(
                compareBy<Task> { it.isCompleted }.thenByDescending { it.createdAt }
            )
            SortOrder.DUE_DATE -> result.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenBy { it.dueDate == null }
                    .thenBy { it.dueDate }
            )
            SortOrder.PRIORITY -> result.sortedWith(
                compareBy<Task> { it.isCompleted }.thenByDescending { it.priority.level }
            )
            SortOrder.ALPHABETICAL -> result.sortedWith(
                compareBy<Task> { it.isCompleted }.thenBy { it.title.lowercase() }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = taskRepository.getTotalTaskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedTodayCount: StateFlow<Int> = taskRepository.getCompletedTodayCount(
        DateUtils.getStartOfDay(), DateUtils.getEndOfDay()
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingCount: StateFlow<Int> = taskRepository.getPendingTaskCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val overdueCount: StateFlow<Int> = taskRepository.getOverdueTaskCount(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
        if (!_isSearchActive.value) _searchQuery.value = ""
    }

    fun setFilter(filter: FilterType, categoryId: Long? = null) {
        _filterType.value = filter
        _filterCategoryId.value = categoryId
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(
                    isCompleted = !task.isCompleted,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApp.instance
                return HomeViewModel(app.taskRepository, app.categoryRepository) as T
            }
        }
    }
}
