package com.example.todolistapp.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.todolistapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(
    private val context: Context
) : SettingsRepository {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ACCENT_COLOR_INDEX = intPreferencesKey("accent_color_index")
        val DEFAULT_PRIORITY = intPreferencesKey("default_priority")
        val DEFAULT_CATEGORY_ID = longPreferencesKey("default_category_id")
    }

    override fun getOnboardingCompleted(): Flow<Boolean> =
        context.dataStore.data.catch { emit(emptyPreferences()) }
            .map { it[Keys.ONBOARDING_COMPLETED] ?: false }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    override fun getAccentColorIndex(): Flow<Int> =
        context.dataStore.data.catch { emit(emptyPreferences()) }
            .map { it[Keys.ACCENT_COLOR_INDEX] ?: 0 }

    override suspend fun setAccentColorIndex(index: Int) {
        context.dataStore.edit { it[Keys.ACCENT_COLOR_INDEX] = index }
    }

    override fun getDefaultPriority(): Flow<Int> =
        context.dataStore.data.catch { emit(emptyPreferences()) }
            .map { it[Keys.DEFAULT_PRIORITY] ?: 0 }

    override suspend fun setDefaultPriority(priority: Int) {
        context.dataStore.edit { it[Keys.DEFAULT_PRIORITY] = priority }
    }

    override fun getDefaultCategoryId(): Flow<Long> =
        context.dataStore.data.catch { emit(emptyPreferences()) }
            .map { it[Keys.DEFAULT_CATEGORY_ID] ?: 1L }

    override suspend fun setDefaultCategoryId(categoryId: Long) {
        context.dataStore.edit { it[Keys.DEFAULT_CATEGORY_ID] = categoryId }
    }
}
