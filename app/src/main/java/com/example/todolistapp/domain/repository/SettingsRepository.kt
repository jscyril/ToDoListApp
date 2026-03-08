package com.example.todolistapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun getAccentColorIndex(): Flow<Int>
    suspend fun setAccentColorIndex(index: Int)
    fun getDefaultPriority(): Flow<Int>
    suspend fun setDefaultPriority(priority: Int)
    fun getDefaultCategoryId(): Flow<Long>
    suspend fun setDefaultCategoryId(categoryId: Long)
}

