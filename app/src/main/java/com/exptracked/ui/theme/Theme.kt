package com.exptracked.ui.theme

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

enum class ThemeMode(val label: String) { LIGHT("Light"), DARK("Dark") }

private fun ExpPalette.toColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = fill,
        onPrimary = onFill,
        secondary = gold,
        onSecondary = onGold,
        background = background,
        onBackground = ink,
        surface = cardAlt,
        onSurface = ink,
        surfaceVariant = card,
        onSurfaceVariant = inkSoft,
        outline = outline,
        error = danger,
    )
}

/** Light theme cards are white on near-white, so they get a soft shadow; dark cards separate by tone. */
@Composable
fun expCardElevation() =
    CardDefaults.cardElevation(defaultElevation = if (LocalExpPalette.current.isDark) 0.dp else 2.dp)

@Composable
fun ExptrackedTheme(mode: ThemeMode = ThemeMode.DARK, content: @Composable () -> Unit) {
    val palette = if (mode == ThemeMode.DARK) DarkPalette else LightPalette
    CompositionLocalProvider(LocalExpPalette provides palette) {
        MaterialTheme(colorScheme = palette.toColorScheme(), content = content)
    }
}