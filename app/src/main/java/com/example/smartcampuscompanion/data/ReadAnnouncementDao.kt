package com.example.smartcampuscompanion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadAnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(readAnnouncement: ReadAnnouncement)

    @Query("SELECT announcementId FROM read_announcements WHERE userEmail = :email")
    fun getReadAnnouncementIds(email: String): Flow<List<Int>>
}
