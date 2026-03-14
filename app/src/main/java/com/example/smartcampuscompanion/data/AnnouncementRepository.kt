package com.example.smartcampuscompanion.data

import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val announcementDao: AnnouncementDao) {
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    suspend fun insert(announcement: Announcement) {
        announcementDao.insert(announcement)
    }

    suspend fun delete(announcement: Announcement) {
        announcementDao.delete(announcement)
    }
}
