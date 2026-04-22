package com.example.smartcampuscompanion.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferencesKeys.DARK_MODE] ?: false }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferencesKeys.NOTIFICATIONS] ?: true }

    suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DARK_MODE] = enabled }
    }

    suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.NOTIFICATIONS] = enabled }
    }
}