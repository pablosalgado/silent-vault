package io.github.pablosalgado.silent.vault.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsEmptyState_whenNoNotifications() {
        composeTestRule.setContent {
            MainScreenContent(
                notifications = emptyList(),
                unreviewedCount = 0,
                hasNotificationAccess = true,
                onNotificationClick = {}
            )
        }

        composeTestRule.onNodeWithText("No notifications yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Notifications you receive will appear here").assertIsDisplayed()
    }

    @Test
    fun showsNotificationList() {
        val testNotifications = listOf(
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
            MainScreenContent(
                notifications = testNotifications,
                unreviewedCount = 0,
                hasNotificationAccess = true,
                onNotificationClick = {}
            )
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()
        composeTestRule.onNodeWithText("World").assertIsDisplayed()
    }

    @Test
    fun showsUnreviewedCount() {
        composeTestRule.setContent {
            MainScreenContent(
                notifications = emptyList(),
                unreviewedCount = 3,
                hasNotificationAccess = true,
                onNotificationClick = {}
            )
        }

        composeTestRule.onNodeWithText("(3)").assertIsDisplayed()
    }

    @Test
    fun hidesUnreviewedCount_whenZero() {
        composeTestRule.setContent {
            MainScreenContent(
                notifications = emptyList(),
                unreviewedCount = 0,
                hasNotificationAccess = true,
                onNotificationClick = {}
            )
        }

        composeTestRule.onNodeWithText("(0)").assertDoesNotExist()
    }

    @Test
    fun showsPermissionBanner_whenAccessDenied() {
        composeTestRule.setContent {
            MainScreenContent(
                notifications = emptyList(),
                unreviewedCount = 0,
                hasNotificationAccess = false,
                onNotificationClick = {}
            )
        }

        composeTestRule.onNodeWithText("Notification access required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grant access").assertIsDisplayed()
    }

    @Test
    fun hidesPermissionBanner_whenAccessGranted() {
        composeTestRule.setContent {
            MainScreenContent(
                notifications = emptyList(),
                unreviewedCount = 0,
                hasNotificationAccess = true,
                onNotificationClick = {}
            )
        }

        composeTestRule.onNodeWithText("Notification access required").assertDoesNotExist()
    }
}
