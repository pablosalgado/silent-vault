package io.github.pablosalgado.silent.vault.service

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import io.github.pablosalgado.silent.vault.data.NotificationRepository
import io.github.pablosalgado.silent.vault.data.local.NotificationDatabase
import io.github.pablosalgado.silent.vault.data.local.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var repository: NotificationRepository

    override fun onListenerConnected() {
        super.onListenerConnected()
        val db = NotificationDatabase.getInstance(this)
        repository = NotificationRepository(db.notificationDao())
        Log.d(TAG, "Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val entity = buildEntity(sbn)
        scope.launch {
            repository.insert(entity)
        }
        cancelNotification(sbn.key)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    internal fun buildEntity(sbn: StatusBarNotification): NotificationEntity {
        val pm = packageManager
        val appName = try {
            val ai = pm.getApplicationInfo(sbn.packageName, 0)
            pm.getApplicationLabel(ai).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            sbn.packageName
        }
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString()
        return NotificationEntity(
            packageName = sbn.packageName,
            appName = appName,
            title = title,
            text = text,
            timestamp = sbn.postTime
        )
    }

    companion object {
        private const val TAG = "NotificationListener"
    }
}
