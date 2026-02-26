package com.fivepartday.alarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fivepartday.alarm.ui.screen.alarmconfig.AlarmConfigScreen
import com.fivepartday.alarm.ui.screen.alarmring.AlarmRingScreen
import com.fivepartday.alarm.ui.screen.home.HomeScreen
import com.fivepartday.alarm.ui.screen.setup.SetupScreen

object Routes {
    const val SETUP = "setup"
    const val ALARM_CONFIG = "alarm_config"
    const val HOME = "home"
    const val ALARM_RING = "alarm_ring"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.SETUP) {
            SetupScreen(
                onNavigateToAlarmConfig = {
                    navController.navigate(Routes.ALARM_CONFIG) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ALARM_CONFIG) {
            AlarmConfigScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ALARM_CONFIG) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToSetup = {
                    navController.navigate(Routes.SETUP)
                },
                onNavigateToAlarmConfig = {
                    navController.navigate(Routes.ALARM_CONFIG)
                }
            )
        }

        composable(Routes.ALARM_RING) {
            AlarmRingScreen(
                onDismiss = {
                    navController.popBackStack()
                    if (navController.currentBackStackEntry == null) {
                        navController.navigate(Routes.HOME)
                    }
                }
            )
        }
    }
}
