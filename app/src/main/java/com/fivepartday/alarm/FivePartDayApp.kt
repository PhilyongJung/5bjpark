package com.fivepartday.alarm

import android.app.Application
import com.fivepartday.alarm.util.NotificationHelper

class FivePartDayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
