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

    private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd

    fun schedule(context: Context, reminder: ServiceReminder) {
        val workManager = WorkManager.getInstance(context)

        val triggerMillis = runCatching {
            val date = LocalDate.parse(reminder.date, DATE_FORMAT)

            // choose the time of day you want notifications to fire:
            val triggerDateTime = LocalDateTime.of(date, LocalTime.of(9, 0)) // 09:00
            triggerDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull() ?: return

        val nowMillis = System.currentTimeMillis()
        val delayMillis = triggerMillis - nowMillis

        // If date/time is in the past, don’t schedule (or schedule immediately if you prefer)
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
            .addTag(reminder.id) // tag with reminder id so we can cancel later
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
