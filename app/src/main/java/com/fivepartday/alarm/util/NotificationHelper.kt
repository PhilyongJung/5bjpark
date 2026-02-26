package com.fivepartday.alarm.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.fivepartday.alarm.MainActivity
import com.fivepartday.alarm.R

object NotificationHelper {

    const val ALARM_CHANNEL_ID = "five_part_day_alarm"
    const val REMINDER_CHANNEL_ID = "five_part_day_reminder"
    const val ALARM_SERVICE_CHANNEL_ID = "alarm_service"

    const val ALARM_NOTIFICATION_ID = 3001
    const val REMINDER_NOTIFICATION_ID = 3002
    const val SERVICE_NOTIFICATION_ID = 3003

    fun createNotificationChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmChannel = NotificationChannel(
            ALARM_CHANNEL_ID,
            "5부제 알람",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "5부제 당일 알람"
            enableVibration(true)
        }

        val reminderChannel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            "5부제 전날 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "내일 5부제 알림"
            enableVibration(true)
        }

        val serviceChannel = NotificationChannel(
            ALARM_SERVICE_CHANNEL_ID,
            "알람 서비스",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "알람 소리 재생 서비스"
        }

        manager.createNotificationChannels(listOf(alarmChannel, reminderChannel, serviceChannel))
    }

    fun buildReminderNotification(context: Context, licensePlate: String): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val lastDigit = licensePlate.lastOrNull() ?: '?'
        return NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("내일 5부제!")
            .setContentText("내일은 끝자리 ${lastDigit}번 차량 5부제 날입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
    }

    fun buildServiceNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, ALARM_SERVICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("5부제 알람")
            .setContentText("알람이 울리고 있습니다")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
    }
}
