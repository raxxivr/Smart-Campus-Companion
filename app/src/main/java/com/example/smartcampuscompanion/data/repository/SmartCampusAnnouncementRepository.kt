package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.AnnouncementDao
import com.example.smartcampuscompanion.data.mapper.toDomain
import com.example.smartcampuscompanion.data.mapper.toEntity
import com.example.smartcampuscompanion.domain.model.Announcement
import com.example.smartcampuscompanion.domain.repository.AnnouncementRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SmartCampusAnnouncementRepository(
    private val firestore: FirebaseFirestore,
    private val announcementDao: AnnouncementDao
) : AnnouncementRepository {

    override fun getAnnouncements(): Flow<List<Announcement>> {
        return announcementDao.getAllAnnouncements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun postAnnouncement(announcement: Announcement) {
        // Local first
        announcementDao.insert(announcement.toEntity())
        
        // Push to Cloud
        try {
            firestore.collection("announcements").add(announcement).await()
        } catch (e: Exception) {
            // Background sync can be handled here or by Member 5 with WorkManager
        }
    }

    override suspend fun deleteAnnouncement(announcement: Announcement) {
        announcementDao.delete(announcement.toEntity())
        // In a real app, you'd also delete from Firestore using the document ID
    }

    override suspend fun syncAnnouncementsWithCloud() {
        try {
            val result = firestore.collection("announcements")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().await()
            
            val cloudData = result.toObjects(Announcement::class.java)
            
            // Refresh local cache
            cloudData.forEach { announcement ->
                announcementDao.insert(announcement.toEntity())
            }
        } catch (e: Exception) {
            // Handle sync failure (e.g., log it)
        }
    }
}
