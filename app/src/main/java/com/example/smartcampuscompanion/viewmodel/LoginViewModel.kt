package com.example.smartcampuscompanion.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.SessionManager
import com.example.smartcampuscompanion.domain.model.User
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
    
    private val _role = mutableStateOf(sessionManager.getRole())
    val role: String? get() = _role.value

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        viewModelScope.launch {
            _isLoading.value = true
            
            // Hardcoded Admin check
            if (trimmedEmail == "admin@smartcampus.com" && trimmedPassword == "admin123") {
                delay(800) // Simulate network for UX
                sessionManager.createLoginSession(
                    fullName = "Admin",
                    email = trimmedEmail,
                    studentNumber = "ADMIN",
                    course = "Administrator",
                    role = "ADMIN"
                )
                _currentUserFullName.value = "Admin"
                _currentUserEmail.value = trimmedEmail
                _currentStudentNumber.value = "ADMIN"
                _currentCourse.value = "Administrator"
                _role.value = "ADMIN"
                
                _isLoggedIn.value = true
                _loginError.value = null
            } else {
                val result = userRepository.loginUser(trimmedEmail, trimmedPassword)

                result.onSuccess { user ->
                    sessionManager.createLoginSession(
                        fullName = user.fullName,
                        email = user.email,
                        studentNumber = user.studentNumber,
                        course = user.course,
                        role = user.role
                    )
                    _currentUserFullName.value = user.fullName
                    _currentUserEmail.value = user.email
                    _currentStudentNumber.value = user.studentNumber
                    _currentCourse.value = user.course
                    _role.value = user.role
                    
                    _isLoggedIn.value = true
                    _loginError.value = null
                }.onFailure { exception ->
                    _loginError.value = exception.message ?: "Login failed"
                }
            }
            _isLoading.value = false
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = userRepository.loginWithGoogle(idToken)
            
            result.onSuccess { user ->
                sessionManager.createLoginSession(
                    fullName = user.fullName,
                    email = user.email,
                    studentNumber = user.studentNumber,
                    course = user.course,
                    role = user.role
                )
                _currentUserFullName.value = user.fullName
                _currentUserEmail.value = user.email
                _currentStudentNumber.value = user.studentNumber
                _currentCourse.value = user.course
                _role.value = user.role
                
                _isLoggedIn.value = true
                _loginError.value = null
            }.onFailure { exception ->
                _loginError.value = exception.message ?: "Google Sign-In failed"
            }
            _isLoading.value = false
        }
    }

    fun updateProfile(user: User, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.updateUserProfile(user)
            if (result.isSuccess) {
                // Update local session and state
                sessionManager.createLoginSession(
                    fullName = user.fullName,
                    email = user.email,
                    studentNumber = user.studentNumber,
                    course = user.course,
                    role = user.role
                )
                _currentUserFullName.value = user.fullName
                _currentStudentNumber.value = user.studentNumber
                _currentCourse.value = user.course
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Give user time to see the loading effect
            userRepository.logout()
            sessionManager.logout()
            _currentUserFullName.value = null
            _currentUserEmail.value = null
            _currentStudentNumber.value = null
            _currentCourse.value = null
            _role.value = "STUDENT"
            _isLoggedIn.value = false
            _isLoading.value = false
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
