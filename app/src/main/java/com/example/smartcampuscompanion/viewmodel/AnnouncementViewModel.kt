package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.AnnouncementRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
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
        readStatusJob = viewModelScope.launch {
            repository.getReadAnnouncementIds(email).collectLatest { ids ->
                _readAnnouncementIds.value = ids.toSet()
            }
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
            repository.insert(
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
            repository.markAsRead(currentUserEmail, announcementId)
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.delete(announcement)
        }
    }
}
