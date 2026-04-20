package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.UserDao
import com.example.smartcampuscompanion.data.mapper.toDomain
import com.example.smartcampuscompanion.data.mapper.toEntity
import com.example.smartcampuscompanion.domain.model.User
import com.example.smartcampuscompanion.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun registerUser(user: User, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("User ID is null"))
            
            // Save to Firestore
            firestore.collection("users").document(uid).set(user).await()
            
            // Save to Local Room
            userDao.insert(user.toEntity(password))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("User ID is null"))
            
            // Fetch from Firestore to get role and full data
            val document = firestore.collection("users").document(uid).get().await()
            
            if (!document.exists()) {
                return Result.failure(Exception("User profile not found in Firestore"))
            }

            // Manual mapping to avoid deserialization errors with data classes
            val user = User(
                email = document.getString("email") ?: "",
                fullName = document.getString("fullName") ?: "",
                studentNumber = document.getString("studentNumber") ?: "",
                course = document.getString("course") ?: "",
                role = document.getString("role") ?: "student"
            )
            
            // Update Local Room
            userDao.insert(user.toEntity(password))
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val uid = firebaseUser.uid
        
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                User(
                    email = document.getString("email") ?: "",
                    fullName = document.getString("fullName") ?: "",
                    studentNumber = document.getString("studentNumber") ?: "",
                    course = document.getString("course") ?: "",
                    role = document.getString("role") ?: "student"
                )
            } else {
                userDao.getUserByEmail(firebaseUser.email ?: "")?.toDomain()
            }
        } catch (e: Exception) {
            // Fallback to local if offline
            userDao.getUserByEmail(firebaseUser.email ?: "")?.toDomain()
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
