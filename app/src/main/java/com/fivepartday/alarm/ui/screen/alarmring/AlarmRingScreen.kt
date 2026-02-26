package com.fivepartday.alarm.ui.screen.alarmring

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fivepartday.alarm.service.AlarmSoundService
import com.fivepartday.alarm.ui.theme.FivePartDayHighlight
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AlarmRingScreen(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = FivePartDayHighlight
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "오늘은 5부제 날입니다!",
                style = MaterialTheme.typography.headlineMedium,
                color = FivePartDayHighlight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "차량 운행에 주의하세요",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    context.stopService(Intent(context, AlarmSoundService::class.java))
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FivePartDayHighlight
                )
            ) {
                Text(
                    text = "알람 끄기",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
