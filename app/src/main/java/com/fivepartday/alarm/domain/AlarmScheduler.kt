package com.fivepartday.alarm.domain

import com.fivepartday.alarm.data.model.AlarmItem

interface AlarmScheduler {
    fun scheduleFivePartDayAlarms(licensePlate: String, alarms: List<AlarmItem>)
    fun scheduleNightReminder(licensePlate: String, hour: Int, minute: Int)
    fun cancelAllAlarms(alarms: List<AlarmItem>)
    fun cancelNightReminder()
    fun rescheduleAll(licensePlate: String, alarms: List<AlarmItem>, nightReminderEnabled: Boolean, nightHour: Int, nightMinute: Int, alarmEnabled: Boolean)
}
