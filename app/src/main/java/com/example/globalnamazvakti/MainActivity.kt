package com.example.globalnamazvakti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.globalnamazvakti.ui.MainScreen
import com.example.globalnamazvakti.ui.theme.GlobalNamazVaktiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlobalNamazVaktiTheme {
                Surface {
                    MainScreen()
                }
            }
        }
    }
}
