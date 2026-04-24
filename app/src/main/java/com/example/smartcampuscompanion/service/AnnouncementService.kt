package com.example.smartcampuscompanion.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smartcampuscompanion.MainActivity
import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.data.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AnnouncementService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var firestore: FirebaseFirestore
    private val CHANNEL_ID = "announcement_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        firestore = Firebase.firestore
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Monitoring campus announcements...")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        startListeningForAnnouncements()

        return START_STICKY
    }

    private fun startListeningForAnnouncements() {
        firestore.collection("announcements")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("AnnouncementService", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    for (dc in snapshots.documentChanges) {
                        if (dc.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                            val announcement = dc.document.toObject(Announcement::class.java)
                            showNewAnnouncementNotification(announcement)
                        }
                    }
                }
            }
    }

    private fun showNewAnnouncementNotification(announcement: Announcement) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("OPEN_ANNOUNCEMENTS", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 
            announcement.id, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("New Announcement: ${announcement.title}")
            .setContentText(announcement.description)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(announcement.id + 100, notification)
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Smart Campus Companion")
            .setContentText(content)
            .setSmallIcon(R.drawable.logo)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Campus Announcement Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
