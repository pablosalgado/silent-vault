package io.github.pablosalgado.silent.vault.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class FakeDao : NotificationDao {

    override suspend fun insert(notification: NotificationEntity): Long = 0

    override fun getAll(): Flow<List<NotificationEntity>> = flowOf(emptyList())

    override fun getUnreviewedCount(): Flow<Int> = flowOf(0)

    override suspend fun markAsReviewed(id: Long) = Unit
}
