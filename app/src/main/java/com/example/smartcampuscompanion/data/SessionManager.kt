package com.example.smartcampuscompanion.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "smart_campus_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_EMAIL = "email"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_STUDENT_NUMBER = "student_number"
        private const val KEY_COURSE = "course"
    }

    fun createLoginSession(fullName: String, email: String, studentNumber: String, course: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_FULL_NAME, fullName)
            putString(KEY_EMAIL, email)
            putString(KEY_STUDENT_NUMBER, studentNumber)
            putString(KEY_COURSE, course)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getFullName(): String? = prefs.getString(KEY_FULL_NAME, null)
    fun getStudentNumber(): String? = prefs.getString(KEY_STUDENT_NUMBER, null)
    fun getCourse(): String? = prefs.getString(KEY_COURSE, null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
