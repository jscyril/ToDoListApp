package com.example.todolistapp.domain.repository

import com.example.todolistapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>>
    fun getActiveTasks(): Flow<List<Task>>
    fun getCompletedTasks(): Flow<List<Task>>
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>>
    fun getTasksDueThisWeek(startOfDay: Long, endOfWeek: Long): Flow<List<Task>>
    fun getOverdueTasks(now: Long): Flow<List<Task>>
    fun getTotalTaskCount(): Flow<Int>
    fun getCompletedTaskCount(): Flow<Int>
    fun getCompletedTodayCount(startOfDay: Long, endOfDay: Long): Flow<Int>
    fun getPendingTaskCount(): Flow<Int>
    fun getOverdueTaskCount(now: Long): Flow<Int>
    fun getTaskCountByCategory(categoryId: Long): Flow<Int>
    fun getTaskCountByPriority(priority: Int): Flow<Int>
    fun getCompletedThisWeekCount(startOfWeek: Long, endOfWeek: Long): Flow<Int>
    suspend fun getTaskById(id: Long): Task?
    fun getTaskByIdFlow(id: Long): Flow<Task?>
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteAllCompletedTasks()
    suspend fun deleteAllTasks()
    suspend fun moveToCategoryOnDelete(categoryId: Long)
}

