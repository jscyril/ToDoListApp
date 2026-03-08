package com.example.todolistapp.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TaskReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong("task_id", -1)
        val taskTitle = inputData.getString("task_title") ?: "Task reminder"

        if (taskId == -1L) return Result.failure()

        NotificationHelper.showTaskReminder(context, taskId, taskTitle)
        return Result.success()
    }
}
