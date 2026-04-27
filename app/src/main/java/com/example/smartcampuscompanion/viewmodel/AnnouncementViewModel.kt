package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.domain.model.Announcement
import com.example.smartcampuscompanion.domain.repository.AnnouncementRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

    val allAnnouncements: StateFlow<List<Announcement>> = repository.getAnnouncements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _readAnnouncementIds = MutableStateFlow<Set<Int>>(emptySet())
    val readAnnouncementIds: StateFlow<Set<Int>> = _readAnnouncementIds.asStateFlow()

    private var currentUserEmail: String = ""
    private var readStatusJob: Job? = null

    fun loadReadStatus(email: String) {
        if (currentUserEmail == email && readStatusJob?.isActive == true) return
        
        currentUserEmail = email
        readStatusJob?.cancel()
        
        // Trigger sync
        syncAnnouncements()
    }

    private fun syncAnnouncements() {
        viewModelScope.launch {
            repository.syncAnnouncementsWithCloud()
        }
    }

    fun clearData() {
        currentUserEmail = ""
        readStatusJob?.cancel()
        _readAnnouncementIds.value = emptySet()
    }

    fun postAnnouncement(title: String, description: String, onResult: (Boolean) -> Unit) {
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            val success = repository.postAnnouncement(
                Announcement(
                    title = title,
                    description = description,
                    date = currentDate
                )
            )
            onResult(success)
        }
    }

    fun updateAnnouncement(announcement: Announcement, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.updateAnnouncement(announcement)
            onResult(success)
        }
    }

    fun markAsRead(announcementId: Int) {
        if (currentUserEmail.isEmpty() || currentUserEmail == "admin@smartcampus.com") return
        viewModelScope.launch {
            // repository.markAsRead(currentUserEmail, announcementId) 
        }
    }

    fun deleteAnnouncement(announcement: Announcement, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.deleteAnnouncement(announcement)
            onResult(success)
        }
    }
}
