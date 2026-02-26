package com.fivepartday.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.scheduler.AlarmSchedulerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = UserPreferencesRepository(context)
                val prefs = repo.userPreferences.first()
                val scheduler = AlarmSchedulerImpl(context)
                scheduler.rescheduleAll(
                    licensePlate = prefs.licensePlate,
                    alarms = prefs.fivePartDayAlarms,
                    nightReminderEnabled = prefs.isNightReminderEnabled,
                    nightHour = prefs.nightReminderHour,
                    nightMinute = prefs.nightReminderMinute,
                    alarmEnabled = prefs.isAlarmEnabled
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
