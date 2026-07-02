package io.github.pablosalgado.silent.vault.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class NotificationListenerTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Test", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    @Test
    fun buildEntity_parsesTitleAndText() {
        val extras = buildExtras {
            setContentTitle("Test Title")
            setContentText("Test Text")
        }
        val entity = buildEntity("com.test", "Test App", extras, 1000L)

        assertEquals("Test Title", entity.title)
        assertEquals("Test Text", entity.text)
        assertEquals(1000L, entity.timestamp)
    }

    @Test
    fun buildEntity_parsesTitleOnly() {
        val extras = buildExtras {
            setContentTitle("Only Title")
        }
        val entity = buildEntity("com.test", "Test", extras, 0L)

        assertEquals("Only Title", entity.title)
        assertNull(entity.text)
    }

    @Test
    fun buildEntity_parsesTextOnly() {
        val extras = buildExtras {
            setContentText("Only Text")
        }
        val entity = buildEntity("com.test", "Test", extras, 0L)

        assertNull(entity.title)
        assertEquals("Only Text", entity.text)
    }

    @Test
    fun buildEntity_handlesNoTitleOrText() {
        val extras = buildExtras {}
        val entity = buildEntity("com.test", "Test", extras, 0L)

        assertNull(entity.title)
        assertNull(entity.text)
    }

    @Test
    fun buildEntity_setsPackageName() {
        val extras = buildExtras {
            setContentTitle("Title")
        }
        val entity = buildEntity("com.custom.pkg", "Test", extras, 0L)

        assertEquals("com.custom.pkg", entity.packageName)
    }

    @Test
    fun buildEntity_setsAppName() {
        val extras = buildExtras {
            setContentTitle("Title")
        }
        val entity = buildEntity("com.test", "My App", extras, 0L)

        assertEquals("My App", entity.appName)
    }

    @Test
    fun buildEntity_setsTimestamp() {
        val extras = buildExtras {
            setContentTitle("Title")
        }
        val entity = buildEntity("com.test", "Test", extras, 5000L)

        assertEquals(5000L, entity.timestamp)
    }

    @Test
    fun resolveAppName_returnsLabel_whenPackageFound() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = resolveAppName(
            context.packageManager,
            context.packageName
        )

        assertEquals(context.getString(io.github.pablosalgado.silent.vault.R.string.app_name), appName)
    }

    @Test
    fun resolveAppName_returnsPackageName_whenPackageNotFound() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = resolveAppName(
            context.packageManager,
            "com.nonexistent.app"
        )

        assertEquals("com.nonexistent.app", appName)
    }

    @Suppress("DEPRECATION")
    private fun buildExtras(builder: Notification.Builder.() -> Unit): Bundle {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val b = Notification.Builder(context)
        b.setChannelId(CHANNEL_ID)
        b.apply(builder)
        return b.build().extras
    }

    companion object {
        private const val CHANNEL_ID = "test_channel"
    }
}
