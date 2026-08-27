package com.volumelock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Tema fijo índigo del design system (sin dynamic color: la app necesita que el
 * verde "bloqueado" y el índigo "acción" no se contaminen con el wallpaper).
 */
@Composable
fun VolumeLockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val vlColors = if (darkTheme) DarkVlColors else LightVlColors

    CompositionLocalProvider(LocalVlColors provides vlColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
