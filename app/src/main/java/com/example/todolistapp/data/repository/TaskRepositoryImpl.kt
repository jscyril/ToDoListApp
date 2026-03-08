package com.example.todolistapp.data.repository

import com.example.todolistapp.data.local.dao.TaskDao
import com.example.todolistapp.data.local.entity.TaskEntity
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.domain.model.Task
import com.example.todolistapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities -> entities.map { it.toDomain() } }

    override fun searchTasks(query: String): Flow<List<Task>> =
        taskDao.searchTasks(query).map { entities -> entities.map { it.toDomain() } }

    override fun getTasksByCategory(categoryId: Long): Flow<List<Task>> =
        taskDao.getTasksByCategory(categoryId).map { entities -> entities.map { it.toDomain() } }

    override fun getActiveTasks(): Flow<List<Task>> =
        taskDao.getActiveTasks().map { entities -> entities.map { it.toDomain() } }

    override fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getCompletedTasks().map { entities -> entities.map { it.toDomain() } }

    override fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<Task>> =
        taskDao.getTasksDueToday(startOfDay, endOfDay).map { entities -> entities.map { it.toDomain() } }

    override fun getTasksDueThisWeek(startOfDay: Long, endOfWeek: Long): Flow<List<Task>> =
        taskDao.getTasksDueThisWeek(startOfDay, endOfWeek).map { entities -> entities.map { it.toDomain() } }

    override fun getOverdueTasks(now: Long): Flow<List<Task>> =
        taskDao.getOverdueTasks(now).map { entities -> entities.map { it.toDomain() } }

    override fun getTotalTaskCount(): Flow<Int> = taskDao.getTotalTaskCount()
    override fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()
    override fun getCompletedTodayCount(startOfDay: Long, endOfDay: Long): Flow<Int> =
        taskDao.getCompletedTodayCount(startOfDay, endOfDay)
    override fun getPendingTaskCount(): Flow<Int> = taskDao.getPendingTaskCount()
    override fun getOverdueTaskCount(now: Long): Flow<Int> = taskDao.getOverdueTaskCount(now)
    override fun getTaskCountByCategory(categoryId: Long): Flow<Int> = taskDao.getTaskCountByCategory(categoryId)
    override fun getTaskCountByPriority(priority: Int): Flow<Int> = taskDao.getTaskCountByPriority(priority)
    override fun getCompletedThisWeekCount(startOfWeek: Long, endOfWeek: Long): Flow<Int> =
        taskDao.getCompletedThisWeekCount(startOfWeek, endOfWeek)

    override suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)?.toDomain()

    override fun getTaskByIdFlow(id: Long): Flow<Task?> =
        taskDao.getTaskByIdFlow(id).map { it?.toDomain() }

    override suspend fun insertTask(task: Task): Long = taskDao.insertTask(task.toEntity())
    override suspend fun updateTask(task: Task) = taskDao.updateTask(task.toEntity())
    override suspend fun deleteTask(task: Task) = taskDao.deleteTask(task.toEntity())
    override suspend fun deleteAllCompletedTasks() = taskDao.deleteAllCompletedTasks()
    override suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
    override suspend fun moveToCategoryOnDelete(categoryId: Long) = taskDao.moveToCategoryOnDelete(categoryId)

    private fun TaskEntity.toDomain() = Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = Priority.fromLevel(priority),
        categoryId = categoryId,
        dueDate = dueDate,
        dueTime = dueTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun Task.toEntity() = TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        priority = priority.level,
        categoryId = categoryId,
        dueDate = dueDate,
        dueTime = dueTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
