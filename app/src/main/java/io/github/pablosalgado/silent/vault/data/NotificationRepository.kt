package io.github.pablosalgado.silent.vault.data

import io.github.pablosalgado.silent.vault.data.local.NotificationDao
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val dao: NotificationDao) {

    suspend fun insert(notification: NotificationEntity): Long = dao.insert(notification)

    fun getAll(): Flow<List<NotificationEntity>> = dao.getAll()

    fun getUnreviewedCount(): Flow<Int> = dao.getUnreviewedCount()

    suspend fun markAsReviewed(id: Long) = dao.markAsReviewed(id)
}
