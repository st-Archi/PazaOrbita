package com.plazaorbita.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PlazaOrange = Color(0xFFE86A33)
private val PlazaOrangeDark = Color(0xFFB94F22)
private val PlazaBackground = Color(0xFFFAF7F2)

private val LightColors = lightColorScheme(
    primary = PlazaOrange,
    secondary = PlazaOrangeDark,
    background = PlazaBackground
)

private val DarkColors = darkColorScheme(
    primary = PlazaOrange,
    secondary = PlazaOrangeDark
)

@Composable
fun PlazaOrbitaTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
