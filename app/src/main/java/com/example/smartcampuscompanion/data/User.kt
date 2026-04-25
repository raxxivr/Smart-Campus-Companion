package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val fullName: String,
    val studentNumber: String,
    val course: String,
    val password: String,
    val role: String = "STUDENT" // Added role field: "STUDENT" or "ADMIN"
)
