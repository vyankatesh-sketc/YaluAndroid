package com.yalu.india.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = YaluNavy,
    onPrimary = YaluSurface,
    primaryContainer = YaluNavyContainer,
    onPrimaryContainer = YaluOnNavyContainer,
    secondary = YaluRed,
    onSecondary = YaluSurface,
    secondaryContainer = YaluRedContainer,
    onSecondaryContainer = YaluOnRedContainer,
    tertiary = YaluPriceGreen,
    background = YaluBackground,
    surface = YaluSurface,
    surfaceVariant = YaluSurfaceVariant,
    onBackground = YaluOnSurface,
    onSurface = YaluOnSurface,
    onSurfaceVariant = YaluOnSurfaceVariant,
    outline = YaluOutline,
    outlineVariant = YaluOutlineVariant
)

private val DarkColorScheme = darkColorScheme(
    primary = YaluNavyLight,
    onPrimary = YaluSurface,
    primaryContainer = YaluNavy,
    onPrimaryContainer = YaluNavyContainer,
    secondary = YaluRedLight,
    onSecondary = YaluSurface,
    secondaryContainer = YaluRedDark,
    onSecondaryContainer = YaluRedContainer,
    tertiary = YaluPriceGreen,
    background = YaluNavyDark,
    surface = YaluNavyDark,
    onBackground = YaluSurface,
    onSurface = YaluSurface
)

@Composable
fun YaluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
