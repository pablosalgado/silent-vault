package io.github.pablosalgado.silent.vault.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotificationEntity::class], version = 1)
abstract class NotificationDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: NotificationDatabase? = null

        fun getInstance(context: Context): NotificationDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NotificationDatabase::class.java,
                    "silent_vault.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
