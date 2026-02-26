package com.fivepartday.alarm.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

object FivePartDayCalculator {

    fun getRestrictedEndDigits(licensePlate: String): Pair<Int, Int>? {
        val lastDigit = licensePlate.lastOrNull()?.digitToIntOrNull() ?: return null
        val pairedDigit = (lastDigit + 5) % 10
        return Pair(lastDigit, pairedDigit)
    }

    fun isFivePartDay(licensePlate: String, date: LocalDate): Boolean {
        if (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) return false
        val (d1, d2) = getRestrictedEndDigits(licensePlate) ?: return false
        val dateEndDigit = date.dayOfMonth % 10
        return dateEndDigit == d1 || dateEndDigit == d2
    }

    fun getFivePartDaysInMonth(licensePlate: String, yearMonth: YearMonth): List<LocalDate> {
        val days = mutableListOf<LocalDate>()
        for (day in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(day)
            if (isFivePartDay(licensePlate, date)) {
                days.add(date)
            }
        }
        return days
    }

    fun getNextFivePartDay(licensePlate: String, from: LocalDate): LocalDate? {
        var date = from
        for (i in 0..365) {
            if (isFivePartDay(licensePlate, date)) return date
            date = date.plusDays(1)
        }
        return null
    }

    fun isFivePartDayTomorrow(licensePlate: String, today: LocalDate): Boolean {
        return isFivePartDay(licensePlate, today.plusDays(1))
    }
}
