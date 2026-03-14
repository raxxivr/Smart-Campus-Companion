package com.example.smartcampuscompanion.data

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) {
        userDao.insert(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}
