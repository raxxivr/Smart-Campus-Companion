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
        
        // Note: Read status is currently local-only in the existing design
        /* readStatusJob = viewModelScope.launch {
            repository.getReadAnnouncementIds(email).collectLatest { ids ->
                _readAnnouncementIds.value = ids.toSet()
            }
        } */
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

    fun postAnnouncement(title: String, description: String) {
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            repository.postAnnouncement(
                Announcement(
                    title = title,
                    description = description,
                    date = currentDate
                )
            )
        }
    }

    fun markAsRead(announcementId: Int) {
        if (currentUserEmail.isEmpty() || currentUserEmail == "admin@smartcampus.com") return
        viewModelScope.launch {
            // repository.markAsRead(currentUserEmail, announcementId) 
            // Note: Update repository interface if markAsRead needs to be part of Domain
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.deleteAnnouncement(announcement)
        }
    }
}
