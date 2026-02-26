package com.fivepartday.alarm.ui.screen.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.domain.FivePartDayCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

data class SetupUiState(
    val plateInput: String = "",
    val lastDigit: Int? = null,
    val pairedDigit: Int? = null,
    val fivePartDays: List<Int> = emptyList(),
    val currentYearMonth: YearMonth = YearMonth.now()
)

class SetupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun updatePlateInput(input: String) {
        _uiState.value = _uiState.value.copy(plateInput = input)

        if (input.length == 4) {
            val digits = FivePartDayCalculator.getRestrictedEndDigits(input)
            if (digits != null) {
                val yearMonth = _uiState.value.currentYearMonth
                val days = FivePartDayCalculator.getFivePartDaysInMonth(input, yearMonth)
                _uiState.value = _uiState.value.copy(
                    lastDigit = digits.first,
                    pairedDigit = digits.second,
                    fivePartDays = days.map { it.dayOfMonth }
                )
            }
        } else {
            _uiState.value = _uiState.value.copy(
                lastDigit = null,
                pairedDigit = null,
                fivePartDays = emptyList()
            )
        }
    }

    fun changeMonth(delta: Int) {
        val newYearMonth = _uiState.value.currentYearMonth.plusMonths(delta.toLong())
        val plate = _uiState.value.plateInput
        val days = if (plate.length == 4) {
            FivePartDayCalculator.getFivePartDaysInMonth(plate, newYearMonth).map { it.dayOfMonth }
        } else emptyList()
        _uiState.value = _uiState.value.copy(
            currentYearMonth = newYearMonth,
            fivePartDays = days
        )
    }

    fun savePlate(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateLicensePlate(_uiState.value.plateInput)
            onComplete()
        }
    }
}
