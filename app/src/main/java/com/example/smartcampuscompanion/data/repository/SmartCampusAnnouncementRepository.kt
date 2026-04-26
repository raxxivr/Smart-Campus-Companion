package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.AnnouncementDao
import com.example.smartcampuscompanion.data.mapper.toDomain
import com.example.smartcampuscompanion.data.mapper.toEntity
import com.example.smartcampuscompanion.domain.model.Announcement
import com.example.smartcampuscompanion.domain.repository.AnnouncementRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SmartCampusAnnouncementRepository(
    private val firestore: FirebaseFirestore,
    private val announcementDao: AnnouncementDao
) : AnnouncementRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun getAnnouncements(): Flow<List<Announcement>> {
        return announcementDao.getAllAnnouncements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun postAnnouncement(announcement: Announcement) {
        try {
            // Use a map for Firestore to ensure correct serialization and avoid data class issues
            val data = mapOf(
                "title" to announcement.title,
                "description" to announcement.description,
                "date" to announcement.date
            )
            firestore.collection("announcements").add(data).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    override suspend fun deleteAnnouncement(announcement: Announcement) {
        try {
            // Find the specific document in Firestore to delete it
            val snapshot = firestore.collection("announcements")
                .whereEqualTo("title", announcement.title)
                .whereEqualTo("description", announcement.description)
                .get().await()
            
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    override suspend fun syncAnnouncementsWithCloud() {
        // Setup real-time listener with manual mapping to prevent deserialization crashes
        firestore.collection("announcements")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                
                val cloudData = snapshot.documents.mapNotNull { doc ->
                    try {
                        Announcement(
                            // Map the Firestore unique string ID to a unique local Int ID
                            id = doc.id.hashCode(),
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            date = doc.getString("date") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                
                repositoryScope.launch {
                    try {
                        // Ensure local database exactly matches cloud state
                        announcementDao.deleteAllAnnouncements()
                        cloudData.forEach { announcement ->
                            announcementDao.insert(announcement.toEntity())
                        }
                    } catch (e: Exception) {
                        // Handle local DB error
                    }
                }
            }
    }
}
