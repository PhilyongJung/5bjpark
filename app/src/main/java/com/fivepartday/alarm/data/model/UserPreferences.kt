package com.fivepartday.alarm.data.model

data class UserPreferences(
    val licensePlate: String = "",
    val fivePartDayAlarms: List<AlarmItem> = listOf(AlarmItem(0, 7, 0, true)),
    val isAlarmEnabled: Boolean = false,
    val isNightReminderEnabled: Boolean = true,
    val nightReminderHour: Int = 22,
    val nightReminderMinute: Int = 0
)
