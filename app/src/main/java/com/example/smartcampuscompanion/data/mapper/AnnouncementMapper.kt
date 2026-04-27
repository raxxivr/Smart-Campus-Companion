package com.example.smartcampuscompanion.data.mapper

import com.example.smartcampuscompanion.data.Announcement as AnnouncementEntity
import com.example.smartcampuscompanion.domain.model.Announcement as AnnouncementDomain

fun AnnouncementEntity.toDomain(): AnnouncementDomain {
    return AnnouncementDomain(
        id = id,
        firestoreId = firestoreId,
        title = title,
        description = description,
        date = date
    )
}

fun AnnouncementDomain.toEntity(): AnnouncementEntity {
    return AnnouncementEntity(
        id = id,
        firestoreId = firestoreId,
        title = title,
        description = description,
        date = date
    )
}
