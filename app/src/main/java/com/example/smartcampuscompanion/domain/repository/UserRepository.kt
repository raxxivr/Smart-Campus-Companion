package com.example.smartcampuscompanion.domain.repository

import com.example.smartcampuscompanion.domain.model.User

interface UserRepository {
    suspend fun registerUser(user: User, password: String): Result<Unit>
    suspend fun loginUser(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun updateUserProfile(user: User): Result<Unit>
}
