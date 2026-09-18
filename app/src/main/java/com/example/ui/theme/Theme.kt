package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class FieldnoteCustomColors(
    val bg: Color,
    val surface: Color,
    val surfaceMuted: Color,
    val border: Color,
    val text: Color,
    val textMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentBorder: Color,
    val danger: Color,
    val dangerSoft: Color,
    val amber: Color,
    val amberSoft: Color,
    val blue: Color,
    val blueSoft: Color,
    val pillBg: Color,
    val pillText: Color,
    val isDark: Boolean
)

val LocalFieldnoteColors = staticCompositionLocalOf {
    FieldnoteCustomColors(
        bg = FieldnoteLightBg,
        surface = FieldnoteLightSurface,
        surfaceMuted = FieldnoteLightSurfaceMuted,
        border = FieldnoteLightBorder,
        text = FieldnoteLightText,
        textMuted = FieldnoteLightTextMuted,
        accent = FieldnoteLightAccent,
        accentSoft = FieldnoteLightAccentSoft,
        accentBorder = FieldnoteLightAccentBorder,
        danger = FieldnoteLightDanger,
        dangerSoft = FieldnoteLightDangerSoft,
        amber = FieldnoteLightAmber,
        amberSoft = FieldnoteLightAmberSoft,
        blue = FieldnoteLightBlue,
        blueSoft = FieldnoteLightBlueSoft,
        pillBg = FieldnoteLightPillBg,
        pillText = FieldnoteLightPillText,
        isDark = false
    )
}

private val LightFieldnoteCustom = FieldnoteCustomColors(
    bg = FieldnoteLightBg,
    surface = FieldnoteLightSurface,
    surfaceMuted = FieldnoteLightSurfaceMuted,
    border = FieldnoteLightBorder,
    text = FieldnoteLightText,
    textMuted = FieldnoteLightTextMuted,
    accent = FieldnoteLightAccent,
    accentSoft = FieldnoteLightAccentSoft,
    accentBorder = FieldnoteLightAccentBorder,
    danger = FieldnoteLightDanger,
    dangerSoft = FieldnoteLightDangerSoft,
    amber = FieldnoteLightAmber,
    amberSoft = FieldnoteLightAmberSoft,
    blue = FieldnoteLightBlue,
    blueSoft = FieldnoteLightBlueSoft,
    pillBg = FieldnoteLightPillBg,
    pillText = FieldnoteLightPillText,
    isDark = false
)

private val DarkFieldnoteCustom = FieldnoteCustomColors(
    bg = CharcoalCanvas,
    surface = CharcoalSurface,
    surfaceMuted = CharcoalSurfaceMuted,
    border = CharcoalBorder,
    text = CharcoalTextPrimary,
    textMuted = CharcoalTextSecondary,
    accent = OliveGreenPrimary,
    accentSoft = OliveGreenContainer,
    accentBorder = OliveGreenGlow,
    danger = CharcoalTerracotta,
    dangerSoft = CharcoalTerracottaSoft,
    amber = CharcoalWarmOchre,
    amberSoft = CharcoalWarmOchreSoft,
    blue = CharcoalSlateBlue,
    blueSoft = CharcoalSlateBlueSoft,
    pillBg = CharcoalPillBg,
    pillText = CharcoalPillText,
    isDark = true
)

/**
 * Custom Material 3 ColorScheme featuring a dark, minimalist palette:
 * - Deep matte charcoal black background & elevated slate-charcoal surfaces
 * - Tactile beveled hairline borders
 * - Refined muted sage / olive green primary accents
 * - High-contrast off-white typography and earthy functional tones
 */
val CharcoalOliveColorScheme = darkColorScheme(
    primary = OliveGreenPrimary,
    onPrimary = OliveGreenOnPrimary,
    primaryContainer = OliveGreenContainer,
    onPrimaryContainer = OliveGreenOnContainer,
    inversePrimary = OliveGreenDark,

    secondary = CharcoalWarmOchre,
    onSecondary = Color(0xFF261D0C),
    secondaryContainer = CharcoalWarmOchreSoft,
    onSecondaryContainer = Color(0xFFEDE0CA),

    tertiary = CharcoalSlateBlue,
    onTertiary = Color(0xFF0F1B24),
    tertiaryContainer = CharcoalSlateBlueSoft,
    onTertiaryContainer = Color(0xFFCCE2F0),

    background = CharcoalCanvas,
    onBackground = CharcoalTextPrimary,

    surface = CharcoalSurface,
    onSurface = CharcoalTextPrimary,
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = CharcoalTextSecondary,
    surfaceTint = OliveGreenPrimary,

    surfaceContainer = CharcoalSurfaceVariant,
    surfaceContainerHigh = CharcoalSurfaceHigh,
    surfaceContainerHighest = Color(0xFF2E3128),
    surfaceContainerLow = CharcoalSurface,
    surfaceContainerLowest = CharcoalCanvas,

    outline = CharcoalBorder,
    outlineVariant = CharcoalBorderSubtle,

    error = CharcoalTerracotta,
    onError = Color(0xFF3B0D05),
    errorContainer = CharcoalTerracottaSoft,
    onErrorContainer = Color(0xFFF2B8AC),

    inverseSurface = Color(0xFFEDECE6),
    inverseOnSurface = Color(0xFF181915),
    scrim = Color(0xFF000000)
)

private val DarkColorScheme = CharcoalOliveColorScheme

private val LightColorScheme = lightColorScheme(
    primary = FieldnoteLightAccent,
    onPrimary = Color.White,
    primaryContainer = FieldnoteLightAccentSoft,
    onPrimaryContainer = FieldnoteLightAccent,
    secondary = FieldnoteLightAmber,
    onSecondary = Color.White,
    tertiary = FieldnoteLightBlue,
    background = FieldnoteLightBg,
    onBackground = FieldnoteLightText,
    surface = FieldnoteLightSurface,
    onSurface = FieldnoteLightText,
    surfaceVariant = FieldnoteLightSurfaceMuted,
    onSurfaceVariant = FieldnoteLightTextMuted,
    outline = FieldnoteLightBorder,
    error = FieldnoteLightDanger,
    errorContainer = FieldnoteLightDangerSoft,
    onError = Color.White,
    onErrorContainer = FieldnoteLightDanger
)

/**
 * Accessor object for Fieldnote custom theme colors.
 */
object FieldnoteTheme {
    val colors: FieldnoteCustomColors
        @Composable
        get() = LocalFieldnoteColors.current
}

/**
 * Accessor object for the custom Charcoal & Olive Minimalist Theme.
 */
object CharcoalOliveTheme {
    val colorScheme
        get() = CharcoalOliveColorScheme

    val colors: FieldnoteCustomColors
        @Composable
        get() = LocalFieldnoteColors.current
}

/**
 * Custom MaterialTheme configuration in Compose enforcing the dark, minimalist
 * Charcoal and Olive Green design aesthetic.
 */
@Composable
fun CharcoalOliveTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalFieldnoteColors provides DarkFieldnoteCustom) {
        MaterialTheme(
            colorScheme = CharcoalOliveColorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Main application theme supporting dynamic switching between Charcoal-Olive Dark
 * and Japanese Stationery Light aesthetics.
 */
@Composable
fun FieldnoteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val customColors = if (darkTheme) DarkFieldnoteCustom else LightFieldnoteCustom
    val colorScheme = if (darkTheme) CharcoalOliveColorScheme else LightColorScheme

    CompositionLocalProvider(LocalFieldnoteColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
