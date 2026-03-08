package com.example.todolistapp.data.local.dao

import androidx.room.*
import com.example.todolistapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskByIdFlow(id: Long): Flow<TaskEntity?>

    @Query(
        "SELECT * FROM tasks WHERE " +
        "(title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') " +
        "ORDER BY isCompleted ASC, createdAt DESC"
    )
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query(
        "SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate >= :startOfDay AND dueDate < :endOfDay " +
        "ORDER BY isCompleted ASC, dueDate ASC"
    )
    fun getTasksDueToday(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query(
        "SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate >= :startOfDay AND dueDate < :endOfWeek " +
        "ORDER BY isCompleted ASC, dueDate ASC"
    )
    fun getTasksDueThisWeek(startOfDay: Long, endOfWeek: Long): Flow<List<TaskEntity>>

    @Query(
        "SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate < :now AND isCompleted = 0 " +
        "ORDER BY dueDate ASC"
    )
    fun getOverdueTasks(now: Long): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND updatedAt >= :startOfDay AND updatedAt < :endOfDay"
    )
    fun getCompletedTodayCount(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTaskCount(): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM tasks WHERE dueDate IS NOT NULL AND dueDate < :now AND isCompleted = 0"
    )
    fun getOverdueTaskCount(now: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE categoryId = :categoryId")
    fun getTaskCountByCategory(categoryId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = :priority")
    fun getTaskCountByPriority(priority: Int): Flow<Int>

    @Query(
        "SELECT COUNT(*) FROM tasks WHERE isCompleted = 1 AND updatedAt >= :startOfWeek AND updatedAt < :endOfWeek"
    )
    fun getCompletedThisWeekCount(startOfWeek: Long, endOfWeek: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("UPDATE tasks SET categoryId = 1 WHERE categoryId = :categoryId")
    suspend fun moveToCategoryOnDelete(categoryId: Long)
}

