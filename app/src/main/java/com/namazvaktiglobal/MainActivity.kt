package com.namazvaktiglobal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.namazvaktiglobal.ui.NamazVaktiGlobalApp
import com.namazvaktiglobal.ui.theme.NamazVaktiGlobalTheme
import com.namazvaktiglobal.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.namazvaktiglobal.data.preferences.ThemeMode

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            NamazVaktiGlobalTheme(darkTheme = darkTheme) {
                NamazVaktiGlobalApp()
            }
        }
    }
}
