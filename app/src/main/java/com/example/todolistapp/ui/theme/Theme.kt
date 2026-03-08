package com.example.todolistapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAccentColor = compositionLocalOf { ElectricCyan }

fun buildColorScheme(accent: Color) = darkColorScheme(
    primary = accent,
    onPrimary = Black,
    primaryContainer = accent.copy(alpha = 0.15f),
    onPrimaryContainer = accent,
    secondary = accent.copy(alpha = 0.7f),
    onSecondary = Black,
    secondaryContainer = accent.copy(alpha = 0.1f),
    onSecondaryContainer = accent,
    tertiary = TextSecondary,
    onTertiary = Black,
    background = Black,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkGrey,
    error = DestructiveRed,
    onError = Black,
    errorContainer = DestructiveRed.copy(alpha = 0.15f),
    onErrorContainer = DestructiveRed,
    inverseSurface = TextPrimary,
    inverseOnSurface = Black,
    inversePrimary = accent,
    surfaceTint = Color.Transparent
)

@Composable
fun ToDoListAppTheme(
    accentColorIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val accent = AccentColors.getOrElse(accentColorIndex) { ElectricCyan }
    val colorScheme = buildColorScheme(accent)

    CompositionLocalProvider(LocalAccentColor provides accent) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}