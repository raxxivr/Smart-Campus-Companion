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

    val userEmail: String?
        get() = sessionManager.getEmail()

    val fullName: String?
        get() = sessionManager.getFullName()

    val studentNumber: String?
        get() = sessionManager.getStudentNumber()

    val course: String?
        get() = sessionManager.getCourse()

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        viewModelScope.launch {
            _isLoading.value = true
            delay(1500)
            
            val storedEmail = sessionManager.getEmail()
            val storedPassword = sessionManager.getStoredPassword()

            if (storedEmail != null && trimmedEmail == storedEmail && trimmedPassword == storedPassword) {
                sessionManager.createLoginSession(trimmedEmail)
                _isLoggedIn.value = true
                _loginError.value = null
            } else if (storedEmail == null) {
                _loginError.value = "No account found. Please sign up first."
            } else {
                _loginError.value = "Invalid Email or Password"
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)
            sessionManager.logout()
            _isLoggedIn.value = false
            _isLoading.value = false
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
