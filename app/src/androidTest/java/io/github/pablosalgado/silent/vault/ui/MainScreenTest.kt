package io.github.pablosalgado.silent.vault.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.pablosalgado.silent.vault.data.NotificationRepository
import io.github.pablosalgado.silent.vault.data.local.FakeDao
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testNotifications = MutableStateFlow(emptyList<NotificationEntity>())
    private val testUnreviewedCount = MutableStateFlow(0)

    private val fakeDao = object : FakeDao() {
        override fun getAll() = testNotifications
        override fun getUnreviewedCount() = testUnreviewedCount
    }

    @Test
    fun showsEmptyState_whenNoNotifications() {
        composeTestRule.setContent {
            MainScreen(MainViewModel(NotificationRepository(fakeDao)))
        }

        composeTestRule.onNodeWithText("No notifications yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications you receive will appear here").assertIsDisplayed()
    }

    @Test
    fun showsNotificationList() {
        testNotifications.value = listOf(
            NotificationEntity(
                id = 1,
                packageName = "com.test",
                appName = "Test App",
                title = "Hello",
                text = "World",
                timestamp = 1000
            )
        )

        composeTestRule.setContent {
            MainScreen(MainViewModel(NotificationRepository(fakeDao)))
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
        composeTestRule.onNodeWithText("World").assertIsDisplayed()
    }

    @Test
    fun showsUnreviewedCount() {
        testUnreviewedCount.value = 3

        composeTestRule.setContent {
            MainScreen(MainViewModel(NotificationRepository(fakeDao)))
        }

        composeTestRule.onNodeWithText("(3)").assertIsDisplayed()
    }

    @Test
    fun hidesUnreviewedCount_whenZero() {
        composeTestRule.setContent {
            MainScreen(MainViewModel(NotificationRepository(fakeDao)))
        }

        composeTestRule.onNodeWithText("(0)").assertDoesNotExist()
    }
}
