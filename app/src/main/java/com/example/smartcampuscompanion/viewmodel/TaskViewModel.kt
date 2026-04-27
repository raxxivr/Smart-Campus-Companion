package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.domain.model.Task
import com.example.smartcampuscompanion.domain.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private var currentUserEmail: String = ""
    private var tasksJob: Job? = null

    fun loadTasksForUser(email: String) {
        if (currentUserEmail == email && tasksJob?.isActive == true) return
        
        currentUserEmail = email
        tasksJob?.cancel()
        
        // Trigger initial sync
        syncTasks()
        
        tasksJob = viewModelScope.launch {
            repository.getAllTasks(email).collectLatest { tasks ->
                _allTasks.value = tasks
            }
        }
    }

    private fun syncTasks() {
        viewModelScope.launch {
            repository.syncTasksWithCloud(currentUserEmail)
        }
    }

    fun addTask(title: String, description: String, dueDate: Long, category: String) {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    userEmail = currentUserEmail,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    category = category
                )
            )
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
