package com.example.smartcampuscompanion.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _notificationsEnabled = mutableStateOf(true)
    val notificationsEnabled: State<Boolean> = _notificationsEnabled

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }
}

