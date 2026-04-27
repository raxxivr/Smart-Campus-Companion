package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.AnnouncementDao
import com.example.smartcampuscompanion.data.mapper.toDomain
import com.example.smartcampuscompanion.data.mapper.toEntity
import com.example.smartcampuscompanion.domain.model.Announcement
import com.example.smartcampuscompanion.domain.repository.AnnouncementRepository
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun postAnnouncement(announcement: Announcement): Boolean {
        return try {
            val data = mapOf(
                "title" to announcement.title,
                "description" to announcement.description,
                "date" to announcement.date
            )
            firestore.collection("announcements").add(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Boolean {
        return try {
            if (announcement.firestoreId.isEmpty()) return false
            val data = mapOf(
                "title" to announcement.title,
                "description" to announcement.description,
                "date" to announcement.date
            )
            firestore.collection("announcements")
                .document(announcement.firestoreId)
                .update(data)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteAnnouncement(announcement: Announcement): Boolean {
        return try {
            if (announcement.firestoreId.isNotEmpty()) {
                firestore.collection("announcements")
                    .document(announcement.firestoreId)
                    .delete()
                    .await()
                true
            } else {
                val snapshot = firestore.collection("announcements")
                    .whereEqualTo("title", announcement.title)
                    .whereEqualTo("description", announcement.description)
                    .get().await()
                
                for (doc in snapshot.documents) {
                    doc.reference.delete().await()
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun syncAnnouncementsWithCloud() {
        firestore.collection("announcements")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                
                val cloudData = snapshot.documents.mapNotNull { doc ->
                    try {
                        Announcement(
                            id = doc.id.hashCode(),
                            firestoreId = doc.id,
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
                        announcementDao.deleteAllAnnouncements()
                        cloudData.forEach { announcement ->
                            announcementDao.insert(announcement.toEntity())
                        }
                    } catch (e: Exception) {
                    }
                }
            }
    }
}
