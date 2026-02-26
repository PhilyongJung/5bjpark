package com.fivepartday.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fivepartday.alarm.service.AlarmSoundService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("alarm_id", -1)
        val licensePlate = intent.getStringExtra("license_plate") ?: ""

        val serviceIntent = Intent(context, AlarmSoundService::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("license_plate", licensePlate)
        }
        context.startForegroundService(serviceIntent)
    }
}
