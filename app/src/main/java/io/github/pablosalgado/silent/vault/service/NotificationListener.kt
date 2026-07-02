package io.github.pablosalgado.silent.vault.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import io.github.pablosalgado.silent.vault.MainActivity
import io.github.pablosalgado.silent.vault.R
import io.github.pablosalgado.silent.vault.data.NotificationRepository
import io.github.pablosalgado.silent.vault.data.local.NotificationDatabase
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var repository: NotificationRepository
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        val db = NotificationDatabase.getInstance(this)
        repository = NotificationRepository(db.notificationDao())
        startForeground(NOTIFICATION_ID, buildPersistentNotification(0))
        Log.d(TAG, "Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val entity = buildEntity(sbn)
        scope.launch {
            repository.insert(entity)
            updatePersistentNotification()
        }
        cancelNotification(sbn.key)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("DEPRECATION")
    private fun buildPersistentNotification(count: Int): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val body = if (count == 0)
            getString(R.string.notification_active)
        else
            resources.getQuantityString(R.plurals.notification_unreviewed, count, count)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
        return builder
            .setContentTitle(getString(R.string.app_name))
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updatePersistentNotification() {
        scope.launch {
            val count = repository.getUnreviewedCount().first()
            val notification = buildPersistentNotification(count)
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    internal fun buildEntity(sbn: StatusBarNotification): NotificationEntity {
        val pm = packageManager
        val appName = resolveAppName(pm, sbn.packageName)
        return buildEntity(sbn.packageName, appName, sbn.notification.extras, sbn.postTime)
    }

    companion object {
        private const val TAG = "NotificationListener"
        private const val CHANNEL_ID = "silent_vault_service"
        private const val NOTIFICATION_ID = 1
    }
}

internal fun resolveAppName(pm: PackageManager, packageName: String): String {
    return try {
        val ai = pm.getApplicationInfo(packageName, 0)
        pm.getApplicationLabel(ai).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    }
}

internal fun buildEntity(
    packageName: String,
    appName: String,
    extras: android.os.Bundle,
    timestamp: Long
): NotificationEntity {
    val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString()
    val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString()
    return NotificationEntity(
        packageName = packageName,
        appName = appName,
        title = title,
        text = text,
        timestamp = timestamp
    )
}
