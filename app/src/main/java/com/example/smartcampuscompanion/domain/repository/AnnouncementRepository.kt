package com.example.smartcampuscompanion.domain.repository

import com.example.smartcampuscompanion.domain.model.Announcement
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun getAnnouncements(): Flow<List<Announcement>>
    suspend fun postAnnouncement(announcement: Announcement): Boolean
    suspend fun updateAnnouncement(announcement: Announcement): Boolean
    suspend fun deleteAnnouncement(announcement: Announcement): Boolean
    suspend fun syncAnnouncementsWithCloud()
}
