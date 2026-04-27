package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.UserDao
import com.example.smartcampuscompanion.data.mapper.toDomain
import com.example.smartcampuscompanion.data.mapper.toEntity
import com.example.smartcampuscompanion.domain.model.User
import com.example.smartcampuscompanion.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
            
            firestore.collection("users").document(uid).set(user).await()
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
            
            val document = firestore.collection("users").document(uid).get().await()
            
            if (!document.exists()) {
                return Result.failure(Exception("User profile not found in Firestore"))
            }

            val user = User(
                email = document.getString("email") ?: "",
                fullName = document.getString("fullName") ?: "",
                studentNumber = document.getString("studentNumber") ?: "",
                course = document.getString("course") ?: "",
                role = document.getString("role") ?: "STUDENT"
            )
            
            userDao.insert(user.toEntity(password))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("Google Sign-In failed"))
            val uid = firebaseUser.uid

            val document = firestore.collection("users").document(uid).get().await()
            
            val user = if (document.exists()) {
                User(
                    email = document.getString("email") ?: firebaseUser.email ?: "",
                    fullName = document.getString("fullName") ?: firebaseUser.displayName ?: "",
                    studentNumber = document.getString("studentNumber") ?: "",
                    course = document.getString("course") ?: "",
                    role = document.getString("role") ?: "STUDENT"
                )
            } else {
                // New User from Google: Create basic profile
                val newUser = User(
                    email = firebaseUser.email ?: "",
                    fullName = firebaseUser.displayName ?: "",
                    studentNumber = "",
                    course = "",
                    role = "STUDENT"
                )
                firestore.collection("users").document(uid).set(newUser).await()
                newUser
            }

            userDao.insert(user.toEntity(""))
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
                    role = document.getString("role") ?: "STUDENT"
                )
            } else {
                userDao.getUserByEmail(firebaseUser.email ?: "")?.toDomain()
            }
        } catch (e: Exception) {
            userDao.getUserByEmail(firebaseUser.email ?: "")?.toDomain()
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            firestore.collection("users").document(uid).set(user).await()
            userDao.insert(user.toEntity("")) // Cache updated profile
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
