package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MaxyDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002233),
    primaryContainer = Color(0xFF004466),
    onPrimaryContainer = Color(0xFF80EAFF),

    secondary = NeonGreen,
    onSecondary = Color(0xFF003820),
    secondaryContainer = Color(0xFF005530),
    onSecondaryContainer = Color(0xFF70FFBA),

    tertiary = NeonCrimson,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5A001C),
    onTertiaryContainer = Color(0xFFFFB3C1),

    background = CyberBackground,
    onBackground = TextPrimary,

    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    outline = BorderCyanGlow,
    outlineVariant = Color(0xFF1E3355)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // MAXY AI is always in futuristic JARVIS HUD dark mode
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaxyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
