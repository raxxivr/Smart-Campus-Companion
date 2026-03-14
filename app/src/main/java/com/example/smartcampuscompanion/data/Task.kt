package com.example.smartcampuscompanion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long, // Store as timestamp
    val category: String,
    val isCompleted: Boolean = false
)
