package com.example.todolistapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.room.Room
import com.example.todolistapp.data.local.datastore.SettingsDataStore
import com.example.todolistapp.data.local.db.TodoDatabase
import com.example.todolistapp.data.repository.CategoryRepositoryImpl
import com.example.todolistapp.data.repository.TaskRepositoryImpl
import com.example.todolistapp.domain.repository.CategoryRepository
import com.example.todolistapp.domain.repository.SettingsRepository
import com.example.todolistapp.domain.repository.TaskRepository

class TodoApp : Application() {

    lateinit var database: TodoDatabase
    lateinit var taskRepository: TaskRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            this,
            TodoDatabase::class.java,
            "todo_database"
        )
            .addCallback(TodoDatabase.Callback { database.categoryDao() })
            .fallbackToDestructiveMigration(false)
            .build()

        taskRepository = TaskRepositoryImpl(database.taskDao())
        categoryRepository = CategoryRepositoryImpl(database.categoryDao())
        settingsRepository = SettingsDataStore(this)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Task Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for task due date reminders"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "task_reminders"
        lateinit var instance: TodoApp
            private set
    }
}
