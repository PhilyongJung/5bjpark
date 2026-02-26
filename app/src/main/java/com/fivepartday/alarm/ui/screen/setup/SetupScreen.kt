package com.fivepartday.alarm.ui.screen.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fivepartday.alarm.ui.component.LicensePlateInput
import com.fivepartday.alarm.ui.component.MonthCalendarView
import com.fivepartday.alarm.ui.theme.FivePartDayHighlight

@Composable
fun SetupScreen(
    onNavigateToAlarmConfig: () -> Unit,
    viewModel: SetupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "5부제 알람",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "차량 번호판 마지막 4자리를 입력하세요",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        LicensePlateInput(
            value = uiState.plateInput,
            onValueChange = { viewModel.updatePlateInput(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.lastDigit != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "5부제 해당 끝자리",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${uiState.lastDigit}  &  ${uiState.pairedDigit}",
                        style = MaterialTheme.typography.displayLarge,
                        color = FivePartDayHighlight
                    )
                    Text(
                        text = "매달 끝자리가 ${uiState.lastDigit} 또는 ${uiState.pairedDigit}인 평일",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                MonthCalendarView(
                    yearMonth = uiState.currentYearMonth,
                    fivePartDays = uiState.fivePartDays.toSet(),
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "이번 달 5부제: ${uiState.fivePartDays.size}일",
                style = MaterialTheme.typography.bodyMedium,
                color = FivePartDayHighlight
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.savePlate { onNavigateToAlarmConfig() } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.plateInput.length == 4
        ) {
            Text("다음", style = MaterialTheme.typography.titleMedium)
        }
    }
}
