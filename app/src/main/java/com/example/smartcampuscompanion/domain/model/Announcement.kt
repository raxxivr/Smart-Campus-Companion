package com.example.smartcampuscompanion.domain.model

data class Announcement(
    val id: Int = 0,
    val firestoreId: String = "",
    val title: String,
    val description: String,
    val date: String
)
