package com.example.smartcampuscompanion.data

import androidx.room.Entity

@Entity(tableName = "read_announcements", primaryKeys = ["userEmail", "announcementId"])
data class ReadAnnouncement(
    val userEmail: String,
    val announcementId: Int
)
