package com.namazvaktiglobal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    secondary = TealSecondary,
    tertiary = AmberAccent
)

private val DarkColors = darkColorScheme(
    primary = TealSecondary,
    secondary = TealPrimary,
    tertiary = AmberAccent
)

@Composable
fun NamazVaktiGlobalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
