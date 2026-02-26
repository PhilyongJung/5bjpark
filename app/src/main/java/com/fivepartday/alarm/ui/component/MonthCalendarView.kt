package com.fivepartday.alarm.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fivepartday.alarm.ui.theme.FivePartDayHighlight
import com.fivepartday.alarm.ui.theme.FivePartDayHighlightLight
import com.fivepartday.alarm.ui.theme.TodayHighlight
import com.fivepartday.alarm.ui.theme.WeekendColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthCalendarView(
    yearMonth: YearMonth,
    fivePartDays: Set<Int>,
    onPreviousMonth: (() -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    // Monday = 0, Sunday = 6
    val startDayOfWeek = (firstDayOfMonth.dayOfWeek.value - 1) // Monday-based offset

    val dayHeaders = listOf("월", "화", "수", "목", "금", "토", "일")

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPreviousMonth?.invoke() },
                enabled = onPreviousMonth != null
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "이전 달",
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = { onNextMonth?.invoke() },
                enabled = onNextMonth != null
            ) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "다음 달",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dayHeaders.forEachIndexed { index, header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelLarge,
                    color = when (index) {
                        5 -> Color(0xFF1976D2) // Saturday
                        6 -> Color(0xFFD32F2F) // Sunday
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        var currentDay = 1
        val totalCells = startDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < startDayOfWeek || currentDay > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val day = currentDay
                        val isFivePartDay = fivePartDays.contains(day)
                        val isToday = yearMonth.year == today.year &&
                                yearMonth.monthValue == today.monthValue &&
                                day == today.dayOfMonth
                        val isWeekend = col == 5 || col == 6

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isFivePartDay) Modifier.background(FivePartDayHighlightLight, CircleShape)
                                    else Modifier
                                )
                                .then(
                                    if (isToday) Modifier.border(2.dp, TodayHighlight, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isFivePartDay || isToday) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isFivePartDay -> FivePartDayHighlight
                                    isWeekend -> WeekendColor
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        currentDay++
                    }
                }
            }
        }
    }
}
