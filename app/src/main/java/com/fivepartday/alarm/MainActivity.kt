package com.fivepartday.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fivepartday.alarm.data.UserPreferencesRepository
import com.fivepartday.alarm.ui.navigation.AppNavigation
import com.fivepartday.alarm.ui.navigation.Routes
import com.fivepartday.alarm.ui.theme.FivePartDayAlarmTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private var alarmShowReceiver: BroadcastReceiver? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op, user can still use the app */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Determine start destination
        val repo = UserPreferencesRepository(this)
        val prefs = runBlocking { repo.userPreferences.first() }
        val startDest = when {
            intent?.action == "com.fivepartday.alarm.SHOW_ALARM" -> Routes.ALARM_RING
            prefs.licensePlate.isEmpty() -> Routes.SETUP
            prefs.isAlarmEnabled -> Routes.HOME
            else -> Routes.ALARM_CONFIG
        }

        setContent {
            FivePartDayAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        startDestination = startDest
                    )

                    // Register receiver for alarm ring screen
                    alarmShowReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            // Turn on screen and show over lock screen
                            window.addFlags(
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            )
                            navController.navigate(Routes.ALARM_RING)
                        }
                    }
                    registerReceiver(
                        alarmShowReceiver,
                        IntentFilter("com.fivepartday.alarm.SHOW_ALARM"),
                        RECEIVER_NOT_EXPORTED
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == "com.fivepartday.alarm.SHOW_ALARM") {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onDestroy() {
        alarmShowReceiver?.let { unregisterReceiver(it) }
        super.onDestroy()
    }
}
