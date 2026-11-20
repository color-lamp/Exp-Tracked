package com.exptracked.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExpPalette(
    val isDark: Boolean,
    val fill: Color,
    val onFill: Color,
    val background: Color,
    val card: Color,
    val cardAlt: Color,
    val outline: Color,
    val hero: Color,
    val heroDeep: Color,
    val heroSoft: Color,
    val onHero: Color,
    val ink: Color,
    val inkSoft: Color,
    val warning: Color,
    val danger: Color,
    val gold: Color,
    val onGold: Color,
    /** One color per ExpenseCategory, in ordinal order (greens + golds + a neutral). */
    val categories: List<Color>,
)

val DarkPalette = ExpPalette(
    isDark = true,
    fill = Color(0xFF00E676),        // Primary on Dark
    onFill = Color(0xFF002114),
    background = Color(0xFF212121),  // Dark Background
    card = Color(0xFF2C2C2C),        // Dark Surface / Card
    cardAlt = Color(0xFF363636),
    outline = Color(0xFF424242),
    hero = Color(0xFF333333),        // charcoal hero surfaces in Dark
    heroDeep = Color(0xFF1A1A1A),
    heroSoft = Color(0xFF8FB9A0),
    onHero = Color(0xFFFFFFFF),
    ink = Color(0xFFFFFFFF),         // On Surface
    inkSoft = Color(0xFFB0B0B0),
    warning = Color(0xFFD4AF37),
    danger = Color(0xFFCF6679),      // Error
    gold = Color(0xFFD4AF37),        // Secondary / Accent on Dark
    onGold = Color(0xFF212121),
    categories = listOf(
        Color(0xFFD4AF37), Color(0xFF00E676), Color(0xFFC9975B),
        Color(0xFF8FB9A0), Color(0xFF5C8F6E), Color(0xFF9E9E9E),
    ),
)

val LightPalette = ExpPalette(
    isDark = false,
    fill = Color(0xFF00796B),        // Primary on Light
    onFill = Color(0xFFFFFFFF),
    background = Color(0xFFFAFAFA),  // Light Background
    card = Color(0xFFFFFFFF),        // Light Surface / Card
    cardAlt = Color(0xFFF0F4F3),
    outline = Color(0xFFDADFDD),
    hero = Color(0xFF00796B),
    heroDeep = Color(0xFF00574D),
    heroSoft = Color(0xFF5C8F6E),
    onHero = Color(0xFFFFFFFF),
    ink = Color(0xFF000000),         // On Surface
    inkSoft = Color(0xFF616161),
    warning = Color(0xFFB8860B),
    danger = Color(0xFFB00020),      // Error
    gold = Color(0xFFB8860B),        // Secondary / Accent on Light
    onGold = Color(0xFF212121),
    categories = listOf(
        Color(0xFFB8860B), Color(0xFF00796B), Color(0xFFB07D4F),
        Color(0xFF5C8F6E), Color(0xFF1E4D2B), Color(0xFF757575),
    ),
)

val LocalExpPalette = staticCompositionLocalOf { DarkPalette }

object ExpColors {
    val Butter: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.fill
    val Green: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.onFill

    val Cream: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.background
    val Sand: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.card
    val SandLight: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.cardAlt
    val Outline: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.outline
    val Olive: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.hero
    val OliveDark: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.heroDeep
    val OliveSoft: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.heroSoft
    val OnOlive: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.onHero
    val Accent: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.fill
    val Ink: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.ink
    val InkSoft: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.inkSoft
    val Warning: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.warning
    val Danger: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.danger
    val Gold: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.gold
    val OnGold: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.onGold
}