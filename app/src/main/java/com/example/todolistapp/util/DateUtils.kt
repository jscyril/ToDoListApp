package com.example.todolistapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getStartOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getEndOfWeek(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 7)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getStartOfWeek(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun formatTime(millis: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun formatDateTime(dateMillis: Long?, timeMillis: Long?): String {
        if (dateMillis == null) return ""
        val datePart = formatDate(dateMillis)
        val timePart = if (timeMillis != null) " at ${formatTime(timeMillis)}" else ""
        return "$datePart$timePart"
    }

    fun formatFullDate(millis: Long): String {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun formatTodayDate(): String {
        val sdf = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good Morning ☀️"
            hour < 17 -> "Good Afternoon 🌤️"
            else -> "Good Evening 🌙"
        }
    }

    fun isOverdue(dueDate: Long?, dueTime: Long?): Boolean {
        if (dueDate == null) return false
        val now = System.currentTimeMillis()
        val dueMillis = if (dueTime != null) {
            val dateCal = Calendar.getInstance().apply { timeInMillis = dueDate }
            val timeCal = Calendar.getInstance().apply { timeInMillis = dueTime }
            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
            dateCal.timeInMillis
        } else {
            val dateCal = Calendar.getInstance().apply { timeInMillis = dueDate }
            dateCal.set(Calendar.HOUR_OF_DAY, 23)
            dateCal.set(Calendar.MINUTE, 59)
            dateCal.timeInMillis
        }
        return now > dueMillis
    }

    fun isToday(dueDate: Long?): Boolean {
        if (dueDate == null) return false
        val startOfDay = getStartOfDay()
        val endOfDay = getEndOfDay()
        return dueDate in startOfDay..endOfDay
    }

    fun combineDateAndTime(dateMillis: Long, timeMillis: Long): Long {
        val dateCal = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val timeCal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
        dateCal.set(Calendar.SECOND, 0)
        dateCal.set(Calendar.MILLISECOND, 0)
        return dateCal.timeInMillis
    }
}

