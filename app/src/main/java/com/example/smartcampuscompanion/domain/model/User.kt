package com.example.smartcampuscompanion.domain.model

data class User(
    val email: String,
    val fullName: String,
    val studentNumber: String,
    val course: String,
    val role: String = "student"
)
