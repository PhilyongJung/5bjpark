package com.fivepartday.alarm.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fivepartday.alarm.data.model.AlarmItem
import com.fivepartday.alarm.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val LICENSE_PLATE = stringPreferencesKey("license_plate")
        val FIVE_PART_DAY_ALARMS = stringPreferencesKey("five_part_day_alarms")
        val IS_ALARM_ENABLED = booleanPreferencesKey("is_alarm_enabled")
        val IS_NIGHT_REMINDER_ENABLED = booleanPreferencesKey("is_night_reminder_enabled")
        val NIGHT_REMINDER_HOUR = intPreferencesKey("night_reminder_hour")
        val NIGHT_REMINDER_MINUTE = intPreferencesKey("night_reminder_minute")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            licensePlate = prefs[Keys.LICENSE_PLATE] ?: "",
            fivePartDayAlarms = deserializeAlarms(prefs[Keys.FIVE_PART_DAY_ALARMS]),
            isAlarmEnabled = prefs[Keys.IS_ALARM_ENABLED] ?: false,
            isNightReminderEnabled = prefs[Keys.IS_NIGHT_REMINDER_ENABLED] ?: true,
            nightReminderHour = prefs[Keys.NIGHT_REMINDER_HOUR] ?: 22,
            nightReminderMinute = prefs[Keys.NIGHT_REMINDER_MINUTE] ?: 0
        )
    }

    suspend fun updateLicensePlate(plate: String) {
        context.dataStore.edit { it[Keys.LICENSE_PLATE] = plate }
    }

    suspend fun updateAlarms(alarms: List<AlarmItem>) {
        context.dataStore.edit { it[Keys.FIVE_PART_DAY_ALARMS] = serializeAlarms(alarms) }
    }

    suspend fun updateAlarmEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_ALARM_ENABLED] = enabled }
    }

    suspend fun updateNightReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.IS_NIGHT_REMINDER_ENABLED] = enabled }
    }

    suspend fun updateNightReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.NIGHT_REMINDER_HOUR] = hour
            it[Keys.NIGHT_REMINDER_MINUTE] = minute
        }
    }

    private fun serializeAlarms(alarms: List<AlarmItem>): String {
        val jsonArray = JSONArray()
        alarms.forEach { jsonArray.put(it.toJson()) }
        return jsonArray.toString()
    }

    private fun deserializeAlarms(json: String?): List<AlarmItem> {
        if (json.isNullOrEmpty()) return listOf(AlarmItem(0, 7, 0, true))
        return try {
            val jsonArray = JSONArray(json)
            (0 until jsonArray.length()).map { AlarmItem.fromJson(jsonArray.getJSONObject(it)) }
        } catch (e: Exception) {
            listOf(AlarmItem(0, 7, 0, true))
        }
    }
}
