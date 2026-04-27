package com.example.smartcampuscompanion.domain.model

data class Task(
    val id: Int = 0,
    val userEmail: String,
    val title: String,
    val description: String,
    val dueDate: Long,
    val category: String,
    val isCompleted: Boolean = false
)
