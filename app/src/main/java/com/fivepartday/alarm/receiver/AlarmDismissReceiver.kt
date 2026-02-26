package com.fivepartday.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fivepartday.alarm.service.AlarmSoundService

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, AlarmSoundService::class.java)
        context.stopService(serviceIntent)
    }
}
