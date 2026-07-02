package io.github.pablosalgado.silent.vault.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Process
import android.service.notification.StatusBarNotification
import androidx.test.core.app.ApplicationProvider
import io.github.pablosalgado.silent.vault.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class NotificationListenerTest {

    private lateinit var listener: NotificationListener
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        listener = NotificationListener()
        val context = ApplicationProvider.getApplicationContext<Context>()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Test", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    @Test
    fun buildEntity_parsesTitleAndText() {
        val notification = buildNotification {
            setContentTitle("Test Title")
            setContentText("Test Text")
        }
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertEquals("Test Title", entity.title)
        assertEquals("Test Text", entity.text)
    }

    @Test
    fun buildEntity_parsesTitleOnly() {
        val notification = buildNotification {
            setContentTitle("Only Title")
        }
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertEquals("Only Title", entity.title)
        assertNull(entity.text)
    }

    @Test
    fun buildEntity_parsesTextOnly() {
        val notification = buildNotification {
            setContentText("Only Text")
        }
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertNull(entity.title)
        assertEquals("Only Text", entity.text)
    }

    @Test
    fun buildEntity_handlesNoTitleOrText() {
        val notification = buildNotification {}
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertNull(entity.title)
        assertNull(entity.text)
    }

    @Test
    fun buildEntity_setsPackageName() {
        val notification = buildNotification {
            setContentTitle("Title")
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertEquals(context.packageName, entity.packageName)
    }

    @Test
    fun buildEntity_setsTimestamp() {
        val notification = buildNotification {
            setContentTitle("Title")
        }
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertEquals(sbn.postTime, entity.timestamp)
    }

    @Test
    fun buildEntity_resolvesAppNameFromPackageManager() {
        val notification = buildNotification {
            setContentTitle("Title")
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sbn = createStatusBarNotification(notification)
        val entity = listener.buildEntity(sbn)

        assertEquals(context.getString(R.string.app_name), entity.appName)
    }

    @Test
    fun buildEntity_usesPackageNameAsFallback_whenAppNotFound() {
        val notification = buildNotification {
            setContentTitle("Title")
        }
        val sbn = StatusBarNotification(
            "com.nonexistent.app",
            "com.nonexistent.app",
            1,
            null,
            1000,
            1000,
            notification,
            Process.myUserHandle(),
            0L
        )
        val entity = listener.buildEntity(sbn)

        assertEquals("com.nonexistent.app", entity.appName)
    }

    @Suppress("DEPRECATION")
    private fun buildNotification(builder: Notification.Builder.() -> Unit): Notification {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val b = Notification.Builder(context)
        b.setChannelId(CHANNEL_ID)
        b.apply(builder)
        return b.build()
    }

    private fun createStatusBarNotification(notification: Notification): StatusBarNotification {
        val context = ApplicationProvider.getApplicationContext<Context>()
        return StatusBarNotification(
            context.packageName,
            context.packageName,
            1,
            null,
            Process.myUid(),
            Process.myPid(),
            notification,
            Process.myUserHandle(),
            System.currentTimeMillis()
        )
    }

    companion object {
        private const val CHANNEL_ID = "test_channel"
    }
}
