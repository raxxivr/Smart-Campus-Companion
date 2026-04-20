package com.example.smartcampuscompanion.data.mapper

import com.example.smartcampuscompanion.data.Task as TaskEntity
import com.example.smartcampuscompanion.domain.model.Task as TaskDomain

fun TaskEntity.toDomain(): TaskDomain {
    return TaskDomain(
        id = id,
        userEmail = userEmail,
        title = title,
        description = description,
        dueDate = dueDate,
        category = category,
        isCompleted = isCompleted
    )
}

fun TaskDomain.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        userEmail = userEmail,
        title = title,
        description = description,
        dueDate = dueDate,
        category = category,
        isCompleted = isCompleted
    )
}
