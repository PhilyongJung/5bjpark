package com.fivepartday.alarm.ui.screen.alarmconfig

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.data.model.AlarmItem
import com.fivepartday.alarm.scheduler.AlarmSchedulerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AlarmConfigUiState(
    val alarms: List<AlarmItem> = listOf(AlarmItem(0, 7, 0, true)),
    val nightReminderEnabled: Boolean = true,
    val nightReminderHour: Int = 22,
    val nightReminderMinute: Int = 0,
    val licensePlate: String = ""
)

class AlarmConfigViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)
    private val scheduler = AlarmSchedulerImpl(application)

    private val _uiState = MutableStateFlow(AlarmConfigUiState())
    val uiState: StateFlow<AlarmConfigUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = repository.userPreferences.first()
            _uiState.value = AlarmConfigUiState(
                alarms = prefs.fivePartDayAlarms,
                nightReminderEnabled = prefs.isNightReminderEnabled,
                nightReminderHour = prefs.nightReminderHour,
                nightReminderMinute = prefs.nightReminderMinute,
                licensePlate = prefs.licensePlate
            )
        }
    }

    fun addAlarm() {
        val current = _uiState.value.alarms
        val newId = (current.maxOfOrNull { it.id } ?: -1) + 1
        val updated = current + AlarmItem(newId, 7, 0, true)
        _uiState.value = _uiState.value.copy(alarms = updated)
    }

    fun removeAlarm(id: Int) {
        val updated = _uiState.value.alarms.filter { it.id != id }
        _uiState.value = _uiState.value.copy(alarms = updated)
    }

    fun updateAlarmTime(id: Int, hour: Int, minute: Int) {
        val updated = _uiState.value.alarms.map {
            if (it.id == id) it.copy(hour = hour, minute = minute) else it
        }
        _uiState.value = _uiState.value.copy(alarms = updated)
    }

    fun updateAlarmEnabled(id: Int, enabled: Boolean) {
        val updated = _uiState.value.alarms.map {
            if (it.id == id) it.copy(enabled = enabled) else it
        }
        _uiState.value = _uiState.value.copy(alarms = updated)
    }

    fun updateNightReminderEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(nightReminderEnabled = enabled)
    }

    fun updateNightReminderTime(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(
            nightReminderHour = hour,
            nightReminderMinute = minute
        )
    }

    fun saveAndSchedule(onComplete: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            repository.updateAlarms(state.alarms)
            repository.updateNightReminderEnabled(state.nightReminderEnabled)
            repository.updateNightReminderTime(state.nightReminderHour, state.nightReminderMinute)
            repository.updateAlarmEnabled(true)

            scheduler.rescheduleAll(
                licensePlate = state.licensePlate,
                alarms = state.alarms,
                nightReminderEnabled = state.nightReminderEnabled,
                nightHour = state.nightReminderHour,
                nightMinute = state.nightReminderMinute,
                alarmEnabled = true
            )
            onComplete()
        }
    }
}
