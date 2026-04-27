package com.example.smartcampuscompanion.data.mapper

import com.example.smartcampuscompanion.data.User as UserEntity
import com.example.smartcampuscompanion.domain.model.User as UserDomain

fun UserEntity.toDomain(): UserDomain {
    return UserDomain(
        email = email,
        fullName = fullName,
        studentNumber = studentNumber,
        course = course,
        role = "student" // Default role, should be updated from Firestore
    )
}

fun UserDomain.toEntity(password: String = ""): UserEntity {
    return UserEntity(
        email = email,
        fullName = fullName,
        studentNumber = studentNumber,
        course = course,
        password = password
    )
}
