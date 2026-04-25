package com.example.smartcampuscompanion.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartcampuscompanion.data.Announcement
import com.example.smartcampuscompanion.data.TaskDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncAnnouncementsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = TaskDatabase.getDatabase(applicationContext)
        val announcementDao = database.announcementDao()
        val firestore = FirebaseFirestore.getInstance()

        return try {
            val snapshot = firestore.collection("announcements")
                .get()
                .await()

            val announcements = snapshot.toObjects(Announcement::class.java)
            
            for (announcement in announcements) {
                announcementDao.insert(announcement)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
