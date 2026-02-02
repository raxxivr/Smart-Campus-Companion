package com.example.smartcampuscompanion.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _isLoggedIn = mutableStateOf(sessionManager.isLoggedIn())
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    val username: String?
        get() = sessionManager.getUsername()

    fun login(username: String, password: String) {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        viewModelScope.launch {
            _isLoading.value = true
            delay(1500) // Simulate network delay for UX
            
            if (trimmedUsername == "student" && trimmedPassword == "1234") {
                sessionManager.createLoginSession(trimmedUsername)
                _isLoggedIn.value = true
                _loginError.value = null
            } else {
                _loginError.value = "Invalid Credentials"
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Simulate delay
            sessionManager.logout()
            _isLoggedIn.value = false
            _isLoading.value = false
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
