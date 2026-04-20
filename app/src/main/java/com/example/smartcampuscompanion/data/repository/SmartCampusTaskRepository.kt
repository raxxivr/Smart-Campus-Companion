package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.TaskDao
import com.example.smartcampuscompanion.data.mapper.toDomain
import com.example.smartcampuscompanion.data.mapper.toEntity
import com.example.smartcampuscompanion.domain.model.Task
import com.example.smartcampuscompanion.domain.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SmartCampusTaskRepository(
    private val firestore: FirebaseFirestore,
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(userEmail: String): Flow<List<Task>> {
        return taskDao.getAllTasks(userEmail).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTask(task: Task) {
        // Local first for instant UI update
        taskDao.insert(task.toEntity())
        
        // Then sync to Firestore
        try {
            firestore.collection("tasks").add(task).await()
        } catch (e: Exception) {
            // Log error or handle retry logic
        }
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
        // Update in Firestore logic here (requires document ID)
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
        // Delete in Firestore logic here
    }

    override suspend fun syncTasksWithCloud(userEmail: String) {
        try {
            val result = firestore.collection("tasks")
                .whereEqualTo("userEmail", userEmail)
                .get().await()
            
            val cloudTasks = result.toObjects(Task::class.java)
            
            // Update local Room with cloud data
            cloudTasks.forEach { task ->
                taskDao.insert(task.toEntity())
            }
        } catch (e: Exception) {
            // Handle sync failure
        }
    }
}
