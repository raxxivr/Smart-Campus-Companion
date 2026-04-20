package com.example.smartcampuscompanion.domain.repository

import com.example.smartcampuscompanion.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(userEmail: String): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun syncTasksWithCloud(userEmail: String)
}
