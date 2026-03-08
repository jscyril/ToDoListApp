package com.example.todolistapp.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolistapp.TodoApp
import com.example.todolistapp.domain.model.Category
import com.example.todolistapp.domain.repository.CategoryRepository
import com.example.todolistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTaskCountForCategory(categoryId: Long): Flow<Int> =
        taskRepository.getTaskCountByCategory(categoryId)

    fun addCategory(name: String, iconName: String, colorHex: String) {
        viewModelScope.launch {
            categoryRepository.insertCategory(
                Category(name = name, iconName = iconName, colorHex = colorHex)
            )
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            taskRepository.moveToCategoryOnDelete(category.id)
            categoryRepository.deleteCategory(category)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = TodoApp.instance
                return CategoriesViewModel(app.categoryRepository, app.taskRepository) as T
            }
        }
    }
}
