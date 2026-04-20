package com.example.smartcampuscompanion.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.SessionManager
import com.example.smartcampuscompanion.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoggedIn = mutableStateOf(sessionManager.isLoggedIn())
    val isLoggedIn: State<Boolean> = _isLoggedIn

    private val _loginError = mutableStateOf<String?>(null)
    val loginError: State<String?> = _loginError

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _currentUserFullName = mutableStateOf(sessionManager.getFullName())
    val fullName: String? get() = _currentUserFullName.value

    private val _currentUserEmail = mutableStateOf(sessionManager.getEmail())
    val userEmail: String? get() = _currentUserEmail.value

    private val _currentStudentNumber = mutableStateOf(sessionManager.getStudentNumber())
    val studentNumber: String? get() = _currentStudentNumber.value

    private val _currentCourse = mutableStateOf(sessionManager.getCourse())
    val course: String? get() = _currentCourse.value

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        viewModelScope.launch {
            _isLoading.value = true
            
            // Hardcoded Admin check
            if (trimmedEmail == "admin@smartcampus.com" && trimmedPassword == "admin123") {
                sessionManager.createLoginSession(
                    fullName = "Admin",
                    email = trimmedEmail,
                    studentNumber = "ADMIN",
                    course = "Administrator"
                )
                _currentUserFullName.value = "Admin"
                _currentUserEmail.value = trimmedEmail
                _currentStudentNumber.value = "ADMIN"
                _currentCourse.value = "Administrator"
                
                _isLoggedIn.value = true
                _loginError.value = null
            } else {
                val result = userRepository.loginUser(trimmedEmail, trimmedPassword)

                result.onSuccess { user ->
                    sessionManager.createLoginSession(
                        fullName = user.fullName,
                        email = user.email,
                        studentNumber = user.studentNumber,
                        course = user.course
                    )
                    _currentUserFullName.value = user.fullName
                    _currentUserEmail.value = user.email
                    _currentStudentNumber.value = user.studentNumber
                    _currentCourse.value = user.course
                    
                    _isLoggedIn.value = true
                    _loginError.value = null
                }.onFailure { exception ->
                    _loginError.value = exception.message ?: "Login failed"
                }
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.logout()
            sessionManager.logout()
            _currentUserFullName.value = null
            _currentUserEmail.value = null
            _currentStudentNumber.value = null
            _currentCourse.value = null
            _isLoggedIn.value = false
            _isLoading.value = false
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
