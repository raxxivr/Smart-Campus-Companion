package com.example.smartcampuscompanion.domain.usecase

import com.example.smartcampuscompanion.domain.model.Task
import com.example.smartcampuscompanion.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(userEmail: String): Flow<List<Task>> {
        return repository.getAllTasks(userEmail)
    }
}
