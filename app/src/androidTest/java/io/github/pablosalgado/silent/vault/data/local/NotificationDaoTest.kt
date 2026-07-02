package io.github.pablosalgado.silent.vault.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NotificationDaoTest {

    private lateinit var database: NotificationDatabase
    private lateinit var dao: NotificationDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NotificationDatabase::class.java
        ).build()
        dao = database.notificationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAll_returnsNotificationsInReverseChronologicalOrder() = runTest {
        val first = NotificationEntity(
            packageName = "com.example.one",
            appName = "App One",
            title = "Title 1",
            text = "Text 1",
            timestamp = 1000
        )
        val second = NotificationEntity(
            packageName = "com.example.two",
            appName = "App Two",
            title = "Title 2",
            text = "Text 2",
            timestamp = 2000
        )

        dao.insert(first)
        dao.insert(second)

        val all = dao.getAll().first()
        assertEquals(2, all.size)
        assertEquals("App Two", all[0].appName)
        assertEquals("App One", all[1].appName)
    }

    @Test
    fun getUnreviewedCount_returnsOnlyUnreviewed() = runTest {
        val reviewed = NotificationEntity(
            packageName = "com.example.one",
            appName = "App One",
            title = "Title",
            text = "Text",
            timestamp = 1000,
            isReviewed = true
        )
        val unreviewed = NotificationEntity(
            packageName = "com.example.two",
            appName = "App Two",
            title = "Title",
            text = "Text",
            timestamp = 2000,
            isReviewed = false
        )

        dao.insert(reviewed)
        dao.insert(unreviewed)

        val count = dao.getUnreviewedCount().first()
        assertEquals(1, count)
    }

    @Test
    fun markAsReviewed_updatesFlag() = runTest {
        val notification = NotificationEntity(
            packageName = "com.example.one",
            appName = "App One",
            title = "Title",
            text = "Text",
            timestamp = 1000
        )
        val id = dao.insert(notification)

        dao.markAsReviewed(id)

        val all = dao.getAll().first()
        assertTrue(all.first().isReviewed)
    }

    @Test
    fun getAll_returnsEmpty_whenNoNotifications() = runTest {
        val all = dao.getAll().first()
        assertTrue(all.isEmpty())
    }
}
