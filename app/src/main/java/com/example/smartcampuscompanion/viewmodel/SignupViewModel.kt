package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignupUiState(
    val fullName: String = "",
    val email: String = "",
    val studentNumber: String = "",
    val course: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isSignupSuccessful: Boolean = false
)

class SignupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onStudentNumberChange(value: String) {
        _uiState.update { it.copy(studentNumber = value, errorMessage = null) }
    }

    fun onCourseChange(value: String) {
        _uiState.update { it.copy(course = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun onSignupClick(
        onSuccess: (String, String, String, String, String) -> Unit
    ) {
        val state = _uiState.value

        val error = when {
            state.fullName.isBlank() -> "Please enter your full name"
            state.email.isBlank() -> "Please enter your email"
            !state.email.contains("@") -> "Please enter a valid email"
            state.studentNumber.isBlank() -> "Please enter your student number"
            state.course.isBlank() -> "Please enter your course"
            state.password.isBlank() -> "Please enter a password"
            state.password.length < 6 -> "Password must be at least 6 characters"
            state.confirmPassword.isBlank() -> "Please confirm your password"
            state.password != state.confirmPassword -> "Passwords do not match"
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        onSuccess(
            state.fullName,
            state.email,
            state.studentNumber,
            state.course,
            state.password
        )

        _uiState.update { it.copy(isSignupSuccessful = true) }
    }

    fun resetSignupSuccess() {
        _uiState.update { it.copy(isSignupSuccessful = false) }
    }
}
