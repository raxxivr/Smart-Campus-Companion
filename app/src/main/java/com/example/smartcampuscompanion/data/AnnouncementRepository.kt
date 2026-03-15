package com.example.smartcampuscompanion.data

import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(
    private val announcementDao: AnnouncementDao,
    private val readAnnouncementDao: ReadAnnouncementDao
) {
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    suspend fun insert(announcement: Announcement) {
        announcementDao.insert(announcement)
    }

    suspend fun delete(announcement: Announcement) {
        announcementDao.delete(announcement)
    }

    suspend fun markAsRead(userEmail: String, announcementId: Int) {
        readAnnouncementDao.insert(ReadAnnouncement(userEmail, announcementId))
    }

    fun getReadAnnouncementIds(userEmail: String): Flow<List<Int>> {
        return readAnnouncementDao.getReadAnnouncementIds(userEmail)
    }
}
