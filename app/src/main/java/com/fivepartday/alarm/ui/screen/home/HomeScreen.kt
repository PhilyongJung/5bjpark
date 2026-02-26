package com.fivepartday.alarm.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fivepartday.alarm.ui.component.MonthCalendarView
import com.fivepartday.alarm.ui.theme.FivePartDayHighlight
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onNavigateToSetup: () -> Unit,
    onNavigateToAlarmConfig: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "5부제 알람",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Row {
                IconButton(onClick = onNavigateToAlarmConfig) {
                    Icon(Icons.Default.Settings, contentDescription = "알람 설정")
                }
                IconButton(onClick = onNavigateToSetup) {
                    Icon(Icons.Default.Edit, contentDescription = "번호판 변경")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today status card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isTodayFivePartDay)
                    MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isTodayFivePartDay) "오늘은 5부제 날입니다!" else "오늘은 5부제가 아닙니다",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (uiState.isTodayFivePartDay)
                        MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "차량번호 끝자리: ${uiState.lastDigit ?: "-"} & ${uiState.pairedDigit ?: "-"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (uiState.isTodayFivePartDay)
                        MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onPrimaryContainer
                )
                uiState.nextFivePartDay?.let { nextDay ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "다음 5부제: ${nextDay.format(DateTimeFormatter.ofPattern("M월 d일 (E)"))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.isTodayFivePartDay)
                            MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alarm ON/OFF
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "알람",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${uiState.alarms.count { it.enabled }}개 활성화",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.isAlarmEnabled,
                    onCheckedChange = { viewModel.toggleAlarmEnabled(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Alarm times
        if (uiState.isAlarmEnabled && uiState.alarms.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5부제 당일 알람",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.alarms.filter { it.enabled }.forEach { alarm ->
                        Text(
                            text = String.format("%02d:%02d", alarm.hour, alarm.minute),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (uiState.nightReminderEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "전날 알림: ${String.format("%02d:%02d", uiState.nightReminderHour, uiState.nightReminderMinute)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            MonthCalendarView(
                yearMonth = uiState.currentYearMonth,
                fivePartDays = uiState.fivePartDays,
                onPreviousMonth = { viewModel.changeMonth(-1) },
                onNextMonth = { viewModel.changeMonth(1) },
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "● 5부제 해당일",
                style = MaterialTheme.typography.bodySmall,
                color = FivePartDayHighlight,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = "○ 오늘",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
