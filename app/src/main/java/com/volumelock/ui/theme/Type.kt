package com.volumelock.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Escala M3 sobre Roboto (fuente de sistema; el design system pide Roboto Flex,
// visualmente equivalente al fallback del sistema). Roboto Mono → FontFamily.Monospace
// para las cifras de volumen y del log (tabular).
// ponytail: usa las fuentes del sistema; empaquetar los TTF de Roboto Flex/Mono si se quiere calzar exacto.

val AppTypography = Typography(
    headlineSmall = TextStyle(fontWeight = FontWeight.W400, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.W400, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.W500, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.W500, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.W400, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.W400, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.W400, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.W500, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.W500, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.W500, fontSize = 11.sp, lineHeight = 16.sp),
)

/** Cifra grande tabular para lecturas de volumen. */
val ReadoutTextStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.W500,
    fontSize = 44.sp,
    lineHeight = 48.sp,
)

/** Cifra mono en línea (valores del log, "12 / 15"). */
val MonoTextStyle = TextStyle(fontFamily = FontFamily.Monospace)
