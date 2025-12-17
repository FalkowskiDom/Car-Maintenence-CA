package ie.setu.carmaintenenceapp.notifications

import android.content.Context
import androidx.work.*
import ie.setu.carmaintenenceapp.ui.viewmodel.ServiceReminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object ReminderScheduler {

    private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun schedule(context: Context, reminder: ServiceReminder) {
        val workManager = WorkManager.getInstance(context)

        val triggerMillis = runCatching {
            val date = LocalDate.parse(reminder.date, DATE_FORMAT)
            val time = LocalTime.parse(reminder.time, DateTimeFormatter.ISO_LOCAL_TIME)

            // choose the time of day you want notifications to fire:
            val triggerDateTime = LocalDateTime.of(date, time)
            triggerDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull() ?: return

        val nowMillis = System.currentTimeMillis()
        val delayMillis = triggerMillis - nowMillis

        if (delayMillis <= 0) return

        val notifId = abs(reminder.id.hashCode())

        val data = workDataOf(
            "title" to reminder.title,
            "desc" to reminder.description,
            "notifId" to notifId
        )

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(reminder.id)
            .build()

        workManager.enqueueUniqueWork(
            "reminder_${reminder.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel(context: Context, reminderId: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(reminderId)
        workManager.cancelUniqueWork("reminder_$reminderId")
    }
}
