package com.fivepartday.alarm.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.fivepartday.alarm.data.model.AlarmItem
import com.fivepartday.alarm.domain.AlarmScheduler
import com.fivepartday.alarm.domain.FivePartDayCalculator
import com.fivepartday.alarm.receiver.AlarmReceiver
import com.fivepartday.alarm.receiver.NightReminderReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    companion object {
        const val BASE_FIVE_PART_ALARM = 2000
        const val NIGHT_REMINDER_CODE = 1002
    }

    override fun scheduleFivePartDayAlarms(licensePlate: String, alarms: List<AlarmItem>) {
        if (!canScheduleExactAlarms()) return
        val today = LocalDate.now()
        alarms.filter { it.enabled }.forEach { alarm ->
            val nextDate = findNextFivePartDayForAlarm(licensePlate, today, alarm.hour, alarm.minute)
            if (nextDate != null) {
                val triggerTime = nextDate
                    .atTime(alarm.hour, alarm.minute)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("alarm_id", alarm.id)
                    putExtra("license_plate", licensePlate)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    BASE_FIVE_PART_ALARM + alarm.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                    pendingIntent
                )
            }
        }
    }

    override fun scheduleNightReminder(licensePlate: String, hour: Int, minute: Int) {
        if (!canScheduleExactAlarms()) return
        val today = LocalDate.now()
        val nextReminderDate = findNextNightReminderDate(licensePlate, today, hour, minute)
        if (nextReminderDate != null) {
            val triggerTime = nextReminderDate
                .atTime(hour, minute)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val intent = Intent(context, NightReminderReceiver::class.java).apply {
                putExtra("license_plate", licensePlate)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NIGHT_REMINDER_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerTime, pendingIntent),
                pendingIntent
            )
        }
    }

    override fun cancelAllAlarms(alarms: List<AlarmItem>) {
        alarms.forEach { alarm ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BASE_FIVE_PART_ALARM + alarm.id,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }

    override fun cancelNightReminder() {
        val intent = Intent(context, NightReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NIGHT_REMINDER_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }

    override fun rescheduleAll(
        licensePlate: String,
        alarms: List<AlarmItem>,
        nightReminderEnabled: Boolean,
        nightHour: Int,
        nightMinute: Int,
        alarmEnabled: Boolean
    ) {
        cancelAllAlarms(alarms)
        cancelNightReminder()

        if (licensePlate.isEmpty() || !alarmEnabled) return

        scheduleFivePartDayAlarms(licensePlate, alarms)
        if (nightReminderEnabled) {
            scheduleNightReminder(licensePlate, nightHour, nightMinute)
        }
    }

    private fun findNextFivePartDayForAlarm(
        licensePlate: String,
        from: LocalDate,
        hour: Int,
        minute: Int
    ): LocalDate? {
        val now = LocalDateTime.now()
        var date = from
        for (i in 0..365) {
            if (FivePartDayCalculator.isFivePartDay(licensePlate, date)) {
                val alarmTime = date.atTime(hour, minute)
                if (alarmTime.isAfter(now)) return date
            }
            date = date.plusDays(1)
        }
        return null
    }

    private fun findNextNightReminderDate(
        licensePlate: String,
        from: LocalDate,
        hour: Int,
        minute: Int
    ): LocalDate? {
        val now = LocalDateTime.now()
        var date = from
        for (i in 0..365) {
            if (FivePartDayCalculator.isFivePartDayTomorrow(licensePlate, date)) {
                val reminderTime = date.atTime(hour, minute)
                if (reminderTime.isAfter(now)) return date
            }
            date = date.plusDays(1)
        }
        return null
    }
}
