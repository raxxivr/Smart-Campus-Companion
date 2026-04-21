package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.*
import com.example.smartcampuscompanion.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val darkModeEnabled: StateFlow<Boolean> = repository.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch { repository.updateNotifications(enabled) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.updateDarkMode(enabled) }
    }
}