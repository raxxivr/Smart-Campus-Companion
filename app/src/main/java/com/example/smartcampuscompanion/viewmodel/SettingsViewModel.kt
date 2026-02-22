package com.example.smartcampuscompanion.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _notificationsEnabled = mutableStateOf(true)
    val notificationsEnabled: State<Boolean> = _notificationsEnabled

    private val _darkModeEnabled = mutableStateOf(false)
    val darkModeEnabled: State<Boolean> = _darkModeEnabled

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }
}

