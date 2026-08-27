package com.volumelock.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Esquema Material 3 derivado de la semilla índigo #4A4FA6 (ver design system).

val LightColors = lightColorScheme(
    primary = Color(0xFF433B82),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDADAFF),
    onPrimaryContainer = Color(0xFF03020F),
    secondary = Color(0xFF45455B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCDDE8),
    onSecondaryContainer = Color(0xFF030306),
    tertiary = Color(0xFF5E3A52),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEBD7E4),
    onTertiaryContainer = Color(0xFF070205),
    error = Color(0xFF811E1B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFD0C9),
    onErrorContainer = Color(0xFF0E0000),
    background = Color(0xFFF8F8F9),
    onBackground = Color(0xFF030304),
    surface = Color(0xFFF8F8F9),
    onSurface = Color(0xFF030304),
    surfaceVariant = Color(0xFFDDDDE3),
    onSurfaceVariant = Color(0xFF2D2D35),
    surfaceDim = Color(0xFFD4D4D6),
    surfaceBright = Color(0xFFF8F8F9),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F2F3),
    surfaceContainer = Color(0xFFEBEBEC),
    surfaceContainerHigh = Color(0xFFE4E4E6),
    surfaceContainerHighest = Color(0xFFDEDEDF),
    outline = Color(0xFF62626F),
    outlineVariant = Color(0xFFBDBDC5),
    inverseSurface = Color(0xFF161617),
    inverseOnSurface = Color(0xFFEEEEEF),
    inversePrimary = Color(0xFFB8B8ED),
    scrim = Color(0xFF000000),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFB8B8ED),
    onPrimary = Color(0xFF141032),
    primaryContainer = Color(0xFF2B2458),
    onPrimaryContainer = Color(0xFFDADAFF),
    secondary = Color(0xFFBCBCCD),
    onSecondary = Color(0xFF15151F),
    secondaryContainer = Color(0xFF2C2C3B),
    onSecondaryContainer = Color(0xFFDCDDE8),
    tertiary = Color(0xFFD0B4C6),
    onTertiary = Color(0xFF200F1B),
    tertiaryContainer = Color(0xFF3E2435),
    onTertiaryContainer = Color(0xFFEBD7E4),
    error = Color(0xFFF1A89F),
    onError = Color(0xFF310202),
    errorContainer = Color(0xFF570E0D),
    onErrorContainer = Color(0xFFFFD0C9),
    background = Color(0xFF010101),
    onBackground = Color(0xFFDEDEDF),
    surface = Color(0xFF010101),
    onSurface = Color(0xFFDEDEDF),
    surfaceVariant = Color(0xFF2D2D35),
    onSurfaceVariant = Color(0xFFBDBDC5),
    surfaceDim = Color(0xFF010101),
    surfaceBright = Color(0xFF1F1F21),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF030304),
    surfaceContainer = Color(0xFF060606),
    surfaceContainerHigh = Color(0xFF0F0F10),
    surfaceContainerHighest = Color(0xFF1A1A1C),
    outline = Color(0xFF7F7F8B),
    outlineVariant = Color(0xFF2D2D35),
    inverseSurface = Color(0xFFDEDEDF),
    inverseOnSurface = Color(0xFF161617),
    inversePrimary = Color(0xFF433B82),
    scrim = Color(0xFF000000),
)

/** Rol semántico extra "candado activo" (verde), fuera del set M3. Ver design system. */
data class VlColors(
    val locked: Color,
    val onLocked: Color,
    val lockedContainer: Color,
    val onLockedContainer: Color,
)

val LightVlColors = VlColors(
    locked = Color(0xFF1E5430),
    onLocked = Color(0xFFFFFFFF),
    lockedContainer = Color(0xFFCDE5D2),
    onLockedContainer = Color(0xFF000501),
)

val DarkVlColors = VlColors(
    locked = Color(0xFFA5C8AD),
    onLocked = Color(0xFF031C0B),
    lockedContainer = Color(0xFF0E371D),
    onLockedContainer = Color(0xFFCDE5D2),
)

val LocalVlColors = staticCompositionLocalOf { LightVlColors }
