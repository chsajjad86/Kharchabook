package com.kharchabook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7DD3FC),
    onPrimary = Color(0xFF001F2A),
    primaryContainer = Color(0xFF114F63),
    onPrimaryContainer = Color(0xFFD0EFFF),
    secondary = Color(0xFFA5D6A7),
    onSecondary = Color(0xFF0B2E12),
    background = Color(0xFF0E0E10),
    onBackground = Color(0xFFE6E6E6),
    surface = Color(0xFF16171A),
    onSurface = Color(0xFFE6E6E6),
    surfaceVariant = Color(0xFF23252A),
    onSurfaceVariant = Color(0xFFB8BAC0),
    error = Color(0xFFFF8A80),
    onError = Color(0xFF3B0907)
)

@Composable
fun KharchaBookTheme(content: @Composable () -> Unit) {
    // Dark-only theme
    MaterialTheme(colorScheme = DarkColors, content = content)
}
