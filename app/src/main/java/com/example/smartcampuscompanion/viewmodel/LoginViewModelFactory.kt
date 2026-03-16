package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanion.data.SessionManager
import com.example.smartcampuscompanion.data.UserRepository

class LoginViewModelFactory(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(sessionManager, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
