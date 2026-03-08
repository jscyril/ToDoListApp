package com.example.todolistapp.domain.model

data class Task(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.LOW,
    val categoryId: Long = 1,
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Priority(val level: Int, val label: String) {
    LOW(0, "Low"),
    MEDIUM(1, "Medium"),
    HIGH(2, "High"),
    URGENT(3, "Urgent");

    companion object {
        fun fromLevel(level: Int): Priority = entries.firstOrNull { it.level == level } ?: LOW
    }
}

