package com.lmw.watermonitorandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lmw.watermonitorandroid.domain.device.ui.AdminScreen
import com.lmw.watermonitorandroid.domain.sensor.ui.MonitorScreen
import com.lmw.watermonitorandroid.domain.system.ui.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Route.MONITOR,
        modifier = modifier
    ) {
        composable(Route.MONITOR) {
            MonitorScreen()
        }
        composable(Route.ADMIN) {
            AdminScreen()
        }
        composable(Route.SETTINGS) {
            SettingsScreen()
        }
    }
}