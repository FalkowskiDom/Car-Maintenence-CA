package ie.setu.carmaintenenceapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context


object NotificationHelper {
    const val CHANNEL_ID = "reminders_channel"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Service Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for car service reminders"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}