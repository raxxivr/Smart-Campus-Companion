package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanion.domain.repository.AnnouncementRepository

class AnnouncementViewModelFactory(private val repository: AnnouncementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
