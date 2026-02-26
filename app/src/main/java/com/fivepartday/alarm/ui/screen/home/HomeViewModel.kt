package com.fivepartday.alarm.ui.screen.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.data.model.AlarmItem
import com.fivepartday.alarm.domain.FivePartDayCalculator
import com.fivepartday.alarm.scheduler.AlarmSchedulerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class HomeUiState(
    val licensePlate: String = "",
    val lastDigit: Int? = null,
    val pairedDigit: Int? = null,
    val isTodayFivePartDay: Boolean = false,
    val nextFivePartDay: LocalDate? = null,
    val fivePartDays: Set<Int> = emptySet(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val alarms: List<AlarmItem> = emptyList(),
    val isAlarmEnabled: Boolean = false,
    val nightReminderEnabled: Boolean = true,
    val nightReminderHour: Int = 22,
    val nightReminderMinute: Int = 0
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)
    private val scheduler = AlarmSchedulerImpl(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userPreferences.collect { prefs ->
                val today = LocalDate.now()
                val yearMonth = YearMonth.now()
                val digits = FivePartDayCalculator.getRestrictedEndDigits(prefs.licensePlate)
                val isTodayFPD = FivePartDayCalculator.isFivePartDay(prefs.licensePlate, today)
                val nextFPD = FivePartDayCalculator.getNextFivePartDay(
                    prefs.licensePlate,
                    if (isTodayFPD) today.plusDays(1) else today
                )
                val daysInMonth = FivePartDayCalculator.getFivePartDaysInMonth(prefs.licensePlate, yearMonth)

                _uiState.value = HomeUiState(
                    licensePlate = prefs.licensePlate,
                    lastDigit = digits?.first,
                    pairedDigit = digits?.second,
                    isTodayFivePartDay = isTodayFPD,
                    nextFivePartDay = nextFPD,
                    fivePartDays = daysInMonth.map { it.dayOfMonth }.toSet(),
                    currentYearMonth = yearMonth,
                    alarms = prefs.fivePartDayAlarms,
                    isAlarmEnabled = prefs.isAlarmEnabled,
                    nightReminderEnabled = prefs.isNightReminderEnabled,
                    nightReminderHour = prefs.nightReminderHour,
                    nightReminderMinute = prefs.nightReminderMinute
                )
            }
        }
    }

    fun toggleAlarmEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAlarmEnabled(enabled)
            val state = _uiState.value
            scheduler.rescheduleAll(
                licensePlate = state.licensePlate,
                alarms = state.alarms,
                nightReminderEnabled = state.nightReminderEnabled,
                nightHour = state.nightReminderHour,
                nightMinute = state.nightReminderMinute,
                alarmEnabled = enabled
            )
        }
    }
}
