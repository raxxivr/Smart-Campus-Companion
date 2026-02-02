package com.example.smartcampuscompanion.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.smartcampuscompanion.data.SessionManager

class LoginViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _isLoggedIn = mutableStateOf(sessionManager.isLoggedIn())
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    val username: String?
        get() = sessionManager.getUsername()

    fun login(username: String, password: String) {
        // Gumamit ng .trim() para tanggalin ang extra spaces sa unahan o hulihan
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername == "student" && trimmedPassword == "1234") {
            sessionManager.createLoginSession(trimmedUsername)
            _isLoggedIn.value = true
            _loginError.value = null
        } else {
            _loginError.value = "Invalid Credentials"
        }
    }

    fun logout() {
        sessionManager.logout()
        _isLoggedIn.value = false
    }

    fun clearError() {
        _loginError.value = null
    }
}
