package io.github.pablosalgado.silent.vault.ui

import io.github.pablosalgado.silent.vault.data.NotificationRepository
import io.github.pablosalgado.silent.vault.data.local.FakeDao
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val testNotifications = MutableStateFlow(
        listOf(
            NotificationEntity(
                id = 1,
                packageName = "com.test",
                appName = "Test",
                title = "Title",
                text = "Text",
                timestamp = 2000
            ),
            NotificationEntity(
                id = 2,
                packageName = "com.test2",
                appName = "Test2",
                title = "Title2",
                text = "Text2",
                timestamp = 1000
            )
        )
    )
    private val testUnreviewedCount = MutableStateFlow(1)

    private val fakeRepository = NotificationRepository(object : FakeDao() {
        override fun getAll() = testNotifications
        override fun getUnreviewedCount() = testUnreviewedCount
    })

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun notifications_emitsFromRepository() = runTest(testDispatcher) {
        advanceUntilIdle()
        val result = viewModel.notifications.value
        assertEquals(2, result.size)
        assertEquals("Test", result[0].appName)
    }

    @Test
    fun unreviewedCount_emitsFromRepository() = runTest(testDispatcher) {
        advanceUntilIdle()
        assertEquals(1, viewModel.unreviewedCount.value)
    }

    @Test
    fun markAsReviewed_delegatesToRepository() = runTest(testDispatcher) {
        var markedId: Long? = null
        val repo = NotificationRepository(object : FakeDao() {
            override suspend fun markAsReviewed(id: Long) {
                markedId = id
            }
        })
        val vm = MainViewModel(repo)

        vm.markAsReviewed(42L)
        advanceUntilIdle()

        assertEquals(42L, markedId)
    }
}
