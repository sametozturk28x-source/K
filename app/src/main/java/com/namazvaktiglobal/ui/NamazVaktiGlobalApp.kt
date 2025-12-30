package com.namazvaktiglobal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.namazvaktiglobal.R
import com.namazvaktiglobal.ui.screens.CalendarScreen
import com.namazvaktiglobal.ui.screens.MainScreen
import com.namazvaktiglobal.ui.screens.QiblaScreen
import com.namazvaktiglobal.ui.screens.SettingsScreen

@Composable
fun NamazVaktiGlobalApp() {
    val navController = rememberNavController()
    val items = listOf(
        AppDestination.Main,
        AppDestination.Calendar,
        AppDestination.Qibla,
        AppDestination.Settings
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { destination ->
                    NavigationBarItem(
                        icon = { destination.icon() },
                        label = { Text(text = stringResource(destination.label)) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(navController = navController, startDestination = AppDestination.Main.route) {
                composable(AppDestination.Main.route) { MainScreen(onNavigate = navController::navigate) }
                composable(AppDestination.Calendar.route) { CalendarScreen() }
                composable(AppDestination.Qibla.route) { QiblaScreen() }
                composable(AppDestination.Settings.route) { SettingsScreen() }
            }
        }
    }
}

sealed class AppDestination(val route: String, val label: Int, val icon: @Composable () -> Unit) {
    data object Main : AppDestination("main", R.string.app_name, { androidx.compose.material3.Icon(Icons.Default.Home, contentDescription = null) })
    data object Calendar : AppDestination("calendar", R.string.calendar_title, { androidx.compose.material3.Icon(Icons.Default.CalendarMonth, contentDescription = null) })
    data object Qibla : AppDestination("qibla", R.string.qibla_title, { androidx.compose.material3.Icon(Icons.Default.Explore, contentDescription = null) })
    data object Settings : AppDestination("settings", R.string.settings_title, { androidx.compose.material3.Icon(Icons.Default.Settings, contentDescription = null) })
}
