package io.github.pablosalgado.silent.vault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isReviewed = 0")
    fun getUnreviewedCount(): Flow<Int>

    @Query("UPDATE notifications SET isReviewed = 1 WHERE id = :id")
    suspend fun markAsReviewed(id: Long)
}
