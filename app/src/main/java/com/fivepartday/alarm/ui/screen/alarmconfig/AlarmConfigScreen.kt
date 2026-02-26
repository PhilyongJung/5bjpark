package com.fivepartday.alarm.ui.screen.alarmconfig

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fivepartday.alarm.ui.component.AlarmItemCard
import com.fivepartday.alarm.ui.component.TimePickerDialog

@Composable
fun AlarmConfigScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AlarmConfigViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var editingAlarmId by remember { mutableStateOf<Int?>(null) }
    var showNightTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "알람 설정",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "5부제 당일에 울릴 알람을 설정하세요",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5부제 당일 알람 섹션
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "5부제 당일 알람",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                uiState.alarms.forEach { alarm ->
                    AlarmItemCard(
                        alarm = alarm,
                        onTimeClick = { editingAlarmId = alarm.id },
                        onEnabledChange = { viewModel.updateAlarmEnabled(alarm.id, it) },
                        onDelete = { viewModel.removeAlarm(alarm.id) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { viewModel.addAlarm() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("알람 추가", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 전날 알림 섹션
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "전날 알림",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Switch(
                        checked = uiState.nightReminderEnabled,
                        onCheckedChange = { viewModel.updateNightReminderEnabled(it) }
                    )
                }

                if (uiState.nightReminderEnabled) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "5부제 전날 이 시간에 알림을 보냅니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showNightTimePicker = true }) {
                        Text(
                            text = String.format(
                                "%02d:%02d",
                                uiState.nightReminderHour,
                                uiState.nightReminderMinute
                            ),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveAndSchedule { onNavigateToHome() } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.alarms.isNotEmpty()
        ) {
            Text("완료", style = MaterialTheme.typography.titleMedium)
        }
    }

    // Time picker dialogs
    editingAlarmId?.let { alarmId ->
        val alarm = uiState.alarms.find { it.id == alarmId }
        if (alarm != null) {
            TimePickerDialog(
                initialHour = alarm.hour,
                initialMinute = alarm.minute,
                onConfirm = { h, m ->
                    viewModel.updateAlarmTime(alarmId, h, m)
                    editingAlarmId = null
                },
                onDismiss = { editingAlarmId = null }
            )
        }
    }

    if (showNightTimePicker) {
        TimePickerDialog(
            initialHour = uiState.nightReminderHour,
            initialMinute = uiState.nightReminderMinute,
            onConfirm = { h, m ->
                viewModel.updateNightReminderTime(h, m)
                showNightTimePicker = false
            },
            onDismiss = { showNightTimePicker = false }
        )
    }
}
