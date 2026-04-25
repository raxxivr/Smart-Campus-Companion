package com.example.smartcampuscompanion.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    private const val SYNC_WORK_NAME = "SyncAnnouncementsWork"

    fun scheduleAnnouncementTasks(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Define Constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        // 1. Periodic Work Request (every 15 minutes)
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncAnnouncementsWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Enqueue unique periodic work to avoid multiple instances
        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )

        // 2. One-time Work Request for cleanup
        val cleanupRequest = OneTimeWorkRequestBuilder<CleanupAnnouncementsWorker>()
            .setConstraints(constraints)
            .build()

        // 3. Example of Chaining: Sync (One-time) followed by Cleanup
        val oneTimeSyncRequest = OneTimeWorkRequestBuilder<SyncAnnouncementsWorker>()
            .setConstraints(constraints)
            .build()

        workManager
            .beginUniqueWork("SyncAndCleanupChain", ExistingWorkPolicy.REPLACE, oneTimeSyncRequest)
            .then(cleanupRequest)
            .enqueue()
    }
}
