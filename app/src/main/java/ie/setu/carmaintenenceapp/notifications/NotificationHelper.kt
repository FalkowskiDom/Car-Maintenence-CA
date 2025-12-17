package ie.setu.carmaintenenceapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


object NotificationHelper {
    //Notification channel ID used for service reminders
    const val CHANNEL_ID = "reminders_channel"

    //Creates the notification channel required. Must be called once when the app starts.
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
}}