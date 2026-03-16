package com.example.smartcampuscompanion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val head: String,
    val email: String,
    val phone: String,
    val description: String
)
