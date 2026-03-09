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