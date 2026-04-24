package com.example.smartcampuscompanion.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartcampuscompanion.data.TaskDatabase

class CleanupAnnouncementsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = TaskDatabase.getDatabase(applicationContext)
        val announcementDao = database.announcementDao()

        return try {
            announcementDao.deleteOldAnnouncements()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
