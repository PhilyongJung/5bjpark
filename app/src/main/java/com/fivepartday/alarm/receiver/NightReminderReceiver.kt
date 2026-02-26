package com.fivepartday.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.scheduler.AlarmSchedulerImpl
import com.fivepartday.alarm.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NightReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val licensePlate = intent.getStringExtra("license_plate") ?: ""

        val notification = NotificationHelper.buildReminderNotification(context, licensePlate).build()
        try {
            NotificationManagerCompat.from(context).notify(
                NotificationHelper.REMINDER_NOTIFICATION_ID, notification
            )
        } catch (_: SecurityException) {
            // Notification permission not granted
        }

        // Reschedule next night reminder
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = UserPreferencesRepository(context)
                val prefs = repo.userPreferences.first()
                if (prefs.isNightReminderEnabled && prefs.isAlarmEnabled) {
                    val scheduler = AlarmSchedulerImpl(context)
                    scheduler.scheduleNightReminder(prefs.licensePlate, prefs.nightReminderHour, prefs.nightReminderMinute)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
