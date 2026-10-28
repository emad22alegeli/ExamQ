package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = PrimaryNavyDark,
    primaryContainer = PrimaryNavyLight,
    onPrimaryContainer = Color.White,
    secondary = SecondaryGold,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = AccentTeal,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = Color(0xFFEF5350)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EEF8),
    onPrimaryContainer = PrimaryNavyDark,
    secondary = SecondaryGoldDark,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGoldLight,
    onSecondaryContainer = Color(0xFF533F00),
    tertiary = AccentTeal,
    onTertiary = Color.White,
    background = SurfacePaper,
    surface = SurfaceCard,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = ErrorRed
)

@Composable
fun IraqiExamMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
