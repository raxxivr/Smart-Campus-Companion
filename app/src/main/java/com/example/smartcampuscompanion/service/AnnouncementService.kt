package com.example.smartcampuscompanion.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smartcampuscompanion.MainActivity
import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.data.SessionManager
import com.example.smartcampuscompanion.data.repository.UserPreferencesRepository
import com.example.smartcampuscompanion.domain.model.Announcement
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AnnouncementService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sessionManager: SessionManager
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private var announcementListener: ListenerRegistration? = null
    private var isFirstSnapshot = true
    
    private val MONITORING_CHANNEL_ID = "monitoring_channel"
    private val ANNOUNCEMENT_CHANNEL_ID = "announcement_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        firestore = Firebase.firestore
        sessionManager = SessionManager(this)
        userPreferencesRepository = UserPreferencesRepository(this)
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createMonitoringNotification("Monitoring campus announcements...")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        startListeningForAnnouncements()

        return START_STICKY
    }

    private fun startListeningForAnnouncements() {
        if (announcementListener != null) return

        isFirstSnapshot = true
        announcementListener = firestore.collection("announcements")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("AnnouncementService", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val adminEmail = "admin@smartcampus.com"
                    val isAdmin = currentUser?.email == adminEmail || sessionManager.getRole() == "ADMIN"

                    serviceScope.launch {
                        // Check if notifications are enabled in user preferences
                        val notificationsEnabled = userPreferencesRepository.notificationsFlow.first()

                        for (dc in snapshots.documentChanges) {
                            if (dc.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                                // Conditions to show notification:
                                // 1. Not the initial batch (old announcements)
                                // 2. User is NOT an admin
                                // 3. Notifications are enabled in Settings
                                // 4. Change didn't originate locally (pending writes)
                                if (!isFirstSnapshot && !isAdmin && notificationsEnabled && !dc.document.metadata.hasPendingWrites()) {
                                    val doc = dc.document
                                    val announcement = Announcement(
                                        id = doc.id.hashCode(),
                                        firestoreId = doc.id,
                                        title = doc.getString("title") ?: "",
                                        description = doc.getString("description") ?: "",
                                        date = doc.getString("date") ?: ""
                                    )
                                    showNewAnnouncementNotification(announcement)
                                }
                            }
                        }
                        isFirstSnapshot = false
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

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, ANNOUNCEMENT_CHANNEL_ID)
            .setContentTitle("New Announcement: ${announcement.title}")
            .setContentText(announcement.description)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(announcement.id, notification)
    }

    private fun createMonitoringNotification(content: String): Notification {
        return NotificationCompat.Builder(this, MONITORING_CHANNEL_ID)
            .setContentTitle("Smart Campus Companion")
            .setContentText(content)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val monitoringChannel = NotificationChannel(
                MONITORING_CHANNEL_ID,
                "Campus Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows that the app is listening for new announcements"
            }

            val alertChannel = NotificationChannel(
                ANNOUNCEMENT_CHANNEL_ID,
                "Campus Announcement Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new campus announcements"
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            manager.createNotificationChannel(monitoringChannel)
            manager.createNotificationChannel(alertChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        announcementListener?.remove()
        serviceScope.cancel()
    }
}
