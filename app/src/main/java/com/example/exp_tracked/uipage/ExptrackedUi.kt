@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

/* ============================================================================================
 *  THEME  (Brand Green + Cage Gold, Light + Dark, serif headings)
 *
 *  Every color goes through ExpColors, which reads the active ExpPalette from composition.
 *  Legacy token names are kept so call sites don't change; their meaning is now:
 *    Butter / Accent = PRIMARY (filled buttons, selected states, links, icons)   Green = content ON primary
 *    Gold / OnGold   = SECONDARY accent (offers, highlights, warnings, scan button)
 *    Cream = app background     Sand = cards     SandLight = inputs / selected tiles / dialogs
 *    Olive / OliveDark / OliveSoft = hero surfaces: teal in Light, charcoal in Dark (bars, budget + health cards)
 *    OnOlive = content on hero surfaces (white)   Ink / InkSoft = primary / secondary text
 *    Danger = error   Warning = gold
 *  Canvas / DrawScope lambdas are not composable: read the color into a val ABOVE the Canvas.
 * ========================================================================================== */

enum class ThemeMode(val label: String) { LIGHT("Light"), DARK("Dark") }

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

val ExpSerif: FontFamily = FontFamily.Serif

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
private fun expCardElevation() =
    CardDefaults.cardElevation(defaultElevation = if (LocalExpPalette.current.isDark) 0.dp else 2.dp)

@Composable
fun ExptrackedTheme(mode: ThemeMode = ThemeMode.DARK, content: @Composable () -> Unit) {
    val palette = if (mode == ThemeMode.DARK) DarkPalette else LightPalette
    CompositionLocalProvider(LocalExpPalette provides palette) {
        MaterialTheme(colorScheme = palette.toColorScheme(), content = content)
    }
}

/* ============================================================================================
 *  MODELS / UI STATE  (swap for your Room entities + ViewModel state)
 * ========================================================================================== */

enum class ExpenseCategory(val label: String, val icon: ImageVector) {
    FOOD("Food", Icons.Outlined.Restaurant),
    TRAVEL("Travel", Icons.Outlined.Flight),
    SHOPPING("Shopping", Icons.Outlined.ShoppingBag),
    BILLS("Bills", Icons.Outlined.Receipt),
    ENTERTAINMENT("Entertainment", Icons.Outlined.SportsEsports),
    OTHER("Other", Icons.Outlined.MoreHoriz);

    /** Theme-aware grey for charts, dots and icons. Composable: hoist above any Canvas. */
    val color: Color @Composable @ReadOnlyComposable get() = LocalExpPalette.current.categories[ordinal]
}

data class ExpenseItem(
    val id: Long,
    val payee: String,
    val amount: Double,
    val category: ExpenseCategory,
    val dateTime: LocalDateTime,
)

data class ExpenseDraft(
    val amount: Double,
    val category: ExpenseCategory,
    val payee: String,
    val note: String = "",
    val dateTime: LocalDateTime = LocalDateTime.now(),
)

data class DailySpend(val label: String, val amount: Double)

data class CategoryTotal(val category: ExpenseCategory, val amount: Double)

enum class OfferType(val label: String) { CARD("Cards"), UPI("UPI"), WALLET("Wallets"), COUPON("Coupons") }

/** Card/UPI/wallet offers can auto-apply on Review & Pay. Coupons (scraped deals) just show a code. */
data class DailyOffer(
    val id: String,
    val provider: String,
    val title: String,
    val details: String,
    val type: OfferType,
    val validTill: LocalDate,
    val minSpend: Double = 0.0,
    val percentOff: Double = 0.0,
    val maxOff: Double = Double.MAX_VALUE,
    val flatOff: Double = 0.0,
    val code: String? = null,
    val brand: Color = Color(0xFF00796B),
) {
    fun savingsOn(amount: Double): Double {
        if (amount < minSpend) return 0.0
        val pct = (amount * percentOff / 100.0).coerceAtMost(maxOff)
        return (pct + flatOff).coerceAtMost(amount)
    }
}

enum class UpiApp(val label: String) {
    GPAY("Google Pay"), PHONEPE("PhonePe"), PAYTM("Paytm"), OTHER("Other UPI App")
}

data class CoachMessage(val text: String, val fromUser: Boolean)

enum class BudgetStatus { SAFE, WARNING, EXCEEDED }

data class BudgetState(
    val spent: Double,
    val limit: Double,
    val overrideEnabled: Boolean = false,
    val warningThreshold: Float = 0.8f,
) {
    val fraction: Float get() = if (limit <= 0.0) 0f else (spent / limit).toFloat()
    val remaining: Double get() = limit - spent
    val status: BudgetStatus
        get() = when {
            limit > 0.0 && fraction >= 1f -> BudgetStatus.EXCEEDED
            limit > 0.0 && fraction >= warningThreshold -> BudgetStatus.WARNING
            else -> BudgetStatus.SAFE
        }
}

sealed interface ExpenseGate {
    data object Allowed : ExpenseGate
    data class AllowedWithWarning(val message: String) : ExpenseGate
    data class Blocked(val message: String) : ExpenseGate
}

/**
 * Hard-limit rule. Blocks any entry made while the limit is already exceeded, or that would push the
 * month over it, unless [BudgetState.overrideEnabled]. Re-run this in your ViewModel / repository too.
 */
fun BudgetState.evaluate(newAmount: Double): ExpenseGate {
    if (limit <= 0.0) return ExpenseGate.Allowed
    val projected = spent + newAmount
    return when {
        projected > limit && overrideEnabled ->
            ExpenseGate.AllowedWithWarning("Override on: this puts you ${(projected - limit).asInr()} over your limit.")
        projected > limit ->
            ExpenseGate.Blocked("Limit reached. This would put you ${(projected - limit).asInr()} over. Turn on override to continue.")
        projected / limit >= warningThreshold ->
            ExpenseGate.AllowedWithWarning("Heads up: this takes you to ${((projected / limit) * 100).roundToInt()}% of your limit.")
        else -> ExpenseGate.Allowed
    }
}

data class DashboardUiState(
    val userName: String,
    val email: String,
    val greeting: String,
    val budget: BudgetState,
    val expenses: List<ExpenseItem>,
    val weeklySpend: List<DailySpend>,
    val offers: List<DailyOffer>,
    val healthScore: Int = 0,
    val savingsGoalCount: Int = 0,
    val coachMessages: List<CoachMessage> = emptyList(),
    /** Set from your contact-picker result; the Add Expense screen opens prefilled, then onPayeeConsumed fires. */
    val pendingPayee: String = "",
    val themeMode: ThemeMode = ThemeMode.DARK,
) {
    val categoryTotals: List<CategoryTotal>
        get() = expenses.groupBy { it.category }
            .map { (cat, items) -> CategoryTotal(cat, items.sumOf { it.amount }) }
            .sortedByDescending { it.amount }
}

data class ExptrackedActions(
    val onPickContact: () -> Unit = {},
    /** Launch the UPI intent here. The UI currently assumes success right after this returns. */
    val onPay: (ExpenseDraft, UpiApp) -> Unit = { _, _ -> },
    /** Persist the expense (Room). Called after pay, or from "Log only". */
    val onSaveExpense: (ExpenseDraft) -> Unit = {},
    val onSetOverride: (Boolean) -> Unit = {},
    val onSetLimit: (Double) -> Unit = {},
    val onApplyOffer: (DailyOffer) -> Unit = {},
    val onAskCoach: (String) -> Unit = {},
    val onPayeeConsumed: () -> Unit = {},
    /** Persist the choice (DataStore) and feed it back via DashboardUiState.themeMode. */
    val onSetThemeMode: (ThemeMode) -> Unit = {},
)

/* ============================================================================================
 *  FORMATTING HELPERS
 * ========================================================================================== */

private val IN_LOCALE: Locale = Locale.forLanguageTag("en-IN")
private val timeFmt = DateTimeFormatter.ofPattern("hh:mm a", IN_LOCALE)
private val dateFmt = DateTimeFormatter.ofPattern("EEE, d MMM", IN_LOCALE)
private val shortDateFmt = DateTimeFormatter.ofPattern("d MMM", IN_LOCALE)
private val fullFmt = DateTimeFormatter.ofPattern("dd MMM yyyy • hh:mm a", IN_LOCALE)

fun Double.asInr(): String {
    val nf = NumberFormat.getNumberInstance(IN_LOCALE).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    return "₹" + nf.format(this)
}

private fun Double.asSignedInr(): String = if (this < 0) "-" + (-this).asInr() else asInr()

private fun dayHeader(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(dateFmt)
    }
}

fun greetingFor(hour: Int): String = when (hour) {
    in 5..11 -> "Good Morning"
    in 12..16 -> "Good Afternoon"
    in 17..21 -> "Good Evening"
    else -> "Good Night"
}

/** Pulls the payee name out of a upi://pay?pa=...&pn=... QR payload; falls back to the raw text. */
fun parsePayee(raw: String): String = runCatching {
    val uri = Uri.parse(raw)
    uri.getQueryParameter("pn")?.takeIf { it.isNotBlank() } ?: uri.getQueryParameter("pa") ?: raw
}.getOrDefault(raw)

/** Gold that reads on the hero surfaces (teal in Light, charcoal in Dark). */
private val HeroGold = Color(0xFFF0D77A)

/** Status tones for the hero card (readable on both the teal and charcoal hero). */
private fun BudgetStatus.onDark(): Color = when (this) {
    BudgetStatus.SAFE -> Color(0xFFB9F6CA)
    BudgetStatus.WARNING -> HeroGold
    BudgetStatus.EXCEEDED -> Color(0xFFFFB3BF)
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/** Route screens have a hero-colored top bar (light icons); tab screens follow the page background. */
@Composable
private fun StatusBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
    }
}

/* ============================================================================================
 *  ROOT + NAVIGATION  (simple back stack; swap for Navigation Compose when you wire the app)
 * ========================================================================================== */

private enum class AppTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    HISTORY("History", Icons.Outlined.History),
    OFFERS("Offers", Icons.Outlined.LocalOffer),
    ACCOUNT("Account", Icons.Outlined.Person),
}

private sealed interface Route {
    data object Scan : Route
    data class Add(val payee: String = "") : Route
    data class Review(val draft: ExpenseDraft) : Route
    data class Success(val draft: ExpenseDraft, val paidWith: UpiApp, val reference: String) : Route
    data object Coach : Route
}

@Composable
fun ExptrackedApp(
    state: DashboardUiState,
    actions: ExptrackedActions = ExptrackedActions(),
    /** Plug CameraX / ML Kit here and call onResult with the decoded QR text. */
    scanner: @Composable (onResult: (String) -> Unit) -> Unit = { SimulatedScanner(it) },
) {
    var tab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var showLimitDialog by rememberSaveable { mutableStateOf(false) }
    val stack = remember { mutableStateListOf<Route>() }
    val clipboard = LocalClipboardManager.current
    StatusBarIcons(darkIcons = stack.isEmpty() && !LocalExpPalette.current.isDark)

    LaunchedEffect(state.pendingPayee) {
        if (state.pendingPayee.isNotBlank()) {
            stack.add(Route.Add(state.pendingPayee))
            actions.onPayeeConsumed()
        }
    }

    BackHandler(enabled = stack.isNotEmpty()) {
        if (stack.lastOrNull() is Route.Success) stack.clear() else stack.removeAt(stack.lastIndex)
    }

    when (val route = stack.lastOrNull()) {
        null -> Scaffold(
            containerColor = ExpColors.Cream,
            bottomBar = {
                ExpBottomBar(selected = tab, onSelect = { tab = it }, onScan = { stack.add(Route.Scan) })
            },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (tab) {
                    AppTab.HOME -> HomeScreen(
                        state = state,
                        onSeeAllHistory = { tab = AppTab.HISTORY },
                        onEditLimit = { showLimitDialog = true },
                        onOverrideChange = actions.onSetOverride,
                        onAvatarClick = { tab = AppTab.ACCOUNT },
                    )
                    AppTab.HISTORY -> HistoryScreen(state.expenses)
                    AppTab.OFFERS -> OffersScreen(
                        offers = state.offers,
                        onApply = { offer ->
                            offer.code?.let { clipboard.setText(AnnotatedString(it)) }
                            actions.onApplyOffer(offer)
                        },
                    )
                    AppTab.ACCOUNT -> AccountScreen(
                        state = state,
                        onEditLimit = { showLimitDialog = true },
                        onOpenCoach = { stack.add(Route.Coach) },
                        onThemeModeChange = actions.onSetThemeMode,
                    )
                }
            }
        }

        Route.Scan -> ScanQrScreen(
            onBack = { stack.removeAt(stack.lastIndex) },
            onScanned = { raw -> stack.add(Route.Add(parsePayee(raw))) },
            onEnterManually = { stack.add(Route.Add()) },
            onPickContact = actions.onPickContact,
            scanner = scanner,
        )

        is Route.Add -> AddExpenseScreen(
            initialPayee = route.payee,
            onBack = { stack.removeAt(stack.lastIndex) },
            onNext = { draft -> stack.add(Route.Review(draft)) },
        )

        is Route.Review -> ReviewPayScreen(
            draft = route.draft,
            budget = state.budget,
            offers = state.offers,
            onBack = { stack.removeAt(stack.lastIndex) },
            onOverrideChange = actions.onSetOverride,
            onProceed = { payable, app ->
                val paid = route.draft.copy(amount = payable)
                actions.onPay(paid, app)
                actions.onSaveExpense(paid)
                // Placeholder reference. Replace with the UPI transaction ref from the intent result.
                val ref = System.currentTimeMillis().toString().takeLast(12).padStart(12, '0')
                stack.add(Route.Success(paid, app, ref))
            },
            onLogOnly = { payable ->
                actions.onSaveExpense(route.draft.copy(amount = payable))
                stack.clear()
            },
        )

        is Route.Success -> PaymentSuccessScreen(
            draft = route.draft,
            paidWith = route.paidWith,
            reference = route.reference,
            onDone = { stack.clear() },
        )

        Route.Coach -> CoachScreen(
            messages = state.coachMessages,
            onBack = { stack.removeAt(stack.lastIndex) },
            onAsk = actions.onAskCoach,
        )
    }

    if (showLimitDialog) {
        EditLimitDialog(
            currentLimit = state.budget.limit,
            onDismiss = { showLimitDialog = false },
            onConfirm = { actions.onSetLimit(it); showLimitDialog = false },
        )
    }
}

/* ============================================================================================
 *  BOTTOM BAR  (Home, History, raised Scan, Offers, Account)
 * ========================================================================================== */

@Composable
private fun ExpBottomBar(selected: AppTab, onSelect: (AppTab) -> Unit, onScan: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        // 88dp so the raised button stays inside the layout bounds (taps outside bounds are dropped).
        Box(Modifier.fillMaxWidth().height(88.dp)) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(ExpColors.Olive),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BarItem(AppTab.HOME, selected, onSelect, Modifier.weight(1f))
                BarItem(AppTab.HISTORY, selected, onSelect, Modifier.weight(1f))
                Spacer(Modifier.weight(1f))
                BarItem(AppTab.OFFERS, selected, onSelect, Modifier.weight(1f))
                BarItem(AppTab.ACCOUNT, selected, onSelect, Modifier.weight(1f))
            }
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(ExpColors.Gold)
                    .border(4.dp, ExpColors.Cream, CircleShape)
                    .clickable(onClick = onScan),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.QrCodeScanner, "Scan QR to pay", tint = ExpColors.OnGold, modifier = Modifier.size(30.dp))
            }
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .background(ExpColors.Olive),
        )
    }
}

@Composable
private fun BarItem(tab: AppTab, selected: AppTab, onSelect: (AppTab) -> Unit, modifier: Modifier = Modifier) {
    val active = tab == selected
    val color = if (active) ExpColors.OnOlive else ExpColors.OnOlive.copy(alpha = 0.55f)
    Column(
        modifier.fillMaxHeight().clickable { onSelect(tab) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(tab.icon, tab.label, tint = color)
        Spacer(Modifier.height(2.dp))
        Text(tab.label, color = color, fontSize = 11.sp, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal)
    }
}

/* ============================================================================================
 *  HOME
 * ========================================================================================== */

@Composable
fun HomeScreen(
    state: DashboardUiState,
    onSeeAllHistory: () -> Unit,
    onEditLimit: () -> Unit,
    onOverrideChange: (Boolean) -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { HomeHeader(state.greeting, state.userName, onAvatarClick) }
        item { BudgetCard(state.budget, onOverrideChange, onEditLimit) }
        item { SpendingTrendCard(state.weeklySpend) }
        item { CategoryBreakdownCard(state.categoryTotals) }
        item { SectionTitle("Recent Transactions", "View All", onSeeAllHistory) }
        items(state.expenses.take(4), key = { it.id }) { TransactionRow(it) }
    }
}

@Composable
fun HomeHeader(greeting: String, userName: String, onAvatarClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("$greeting,", color = ExpColors.InkSoft, fontSize = 14.sp)
            Text(userName, color = ExpColors.Ink, fontSize = 30.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
        }
        IconButton(onClick = { /* TODO notifications */ }) {
            Icon(Icons.Outlined.Notifications, "Notifications", tint = ExpColors.Ink)
        }
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(ExpColors.Butter).clickable(onClick = onAvatarClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(userName.take(1).uppercase(), color = ExpColors.Green, fontWeight = FontWeight.Bold)
        }
    }
}

/** Monthly spent vs limit. Accent shifts sage -> gold -> terracotta; exceeded shows the override switch. */
@Composable
fun BudgetCard(
    budget: BudgetState,
    onOverrideChange: (Boolean) -> Unit,
    onEditLimit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = budget.status.onDark()
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(ExpColors.OliveDark, ExpColors.Olive)))
            .padding(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Monthly Budget", color = ExpColors.OnOlive.copy(alpha = 0.8f), fontSize = 14.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.AccountBalanceWallet, null, tint = accent)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text(
                        budget.remaining.asSignedInr(),
                        color = ExpColors.OnOlive,
                        fontSize = 34.sp,
                        fontFamily = ExpSerif,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        if (budget.remaining >= 0) "left this month" else "over your limit",
                        color = ExpColors.OnOlive.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                    )
                }
                Row(Modifier.clickable(onClick = onEditLimit), verticalAlignment = Alignment.CenterVertically) {
                    Text("Limit ${budget.limit.asInr()}", color = ExpColors.OnOlive.copy(alpha = 0.8f), fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Outlined.Edit, "Edit limit", tint = ExpColors.OnOlive.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                }
            }
            LimitProgressBar(budget.fraction, accent)
            Text("Spent ${budget.spent.asInr()}", color = ExpColors.OnOlive, fontSize = 13.sp)

            when (budget.status) {
                BudgetStatus.WARNING -> StatusBanner(
                    Icons.Outlined.Warning,
                    "You've used ${(budget.fraction * 100).roundToInt()}% of your monthly limit.",
                    accent,
                )
                BudgetStatus.EXCEEDED -> {
                    StatusBanner(
                        Icons.Outlined.Block,
                        if (budget.overrideEnabled) "Limit exceeded. Override is on, logging allowed."
                        else "Limit exceeded. New expenses are locked.",
                        accent,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Allow logging anyway", color = ExpColors.OnOlive, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Switch(
                            checked = budget.overrideEnabled,
                            onCheckedChange = onOverrideChange,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = accent,
                                checkedThumbColor = ExpColors.OliveDark,
                                uncheckedTrackColor = Color.Transparent,
                                uncheckedBorderColor = ExpColors.OnOlive.copy(alpha = 0.5f),
                                uncheckedThumbColor = ExpColors.OnOlive,
                            ),
                        )
                    }
                }
                BudgetStatus.SAFE -> Unit
            }
        }
    }
}

@Composable
private fun LimitProgressBar(fraction: Float, color: Color) {
    Box(
        Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)).background(ExpColors.OnOlive.copy(alpha = 0.18f)),
    ) {
        Box(
            Modifier.fillMaxWidth(fraction.coerceIn(0f, 1f)).height(10.dp).clip(RoundedCornerShape(50)).background(color),
        )
    }
}

@Composable
private fun StatusBanner(icon: ImageVector, text: String, color: Color) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(color.copy(alpha = 0.16f)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = color, fontSize = 13.sp)
    }
}

@Composable
fun SpendingTrendCard(points: List<DailySpend>, modifier: Modifier = Modifier) {
    var selected by remember { mutableIntStateOf((points.size / 2).coerceAtLeast(0)) }
    val sel = selected.coerceIn(0, (points.size - 1).coerceAtLeast(0))
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation(),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Spending Trend", color = ExpColors.Ink, fontSize = 17.sp, fontFamily = ExpSerif, modifier = Modifier.weight(1f))
                Text("This Week", color = ExpColors.InkSoft, fontSize = 12.sp)
            }
            if (points.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("-" + points[sel].amount.asInr(), color = ExpColors.Danger, fontWeight = FontWeight.SemiBold)
                Text(points[sel].label, color = ExpColors.InkSoft, fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))
            SpendLineChart(points, sel, { selected = it }, Modifier.fillMaxWidth().height(130.dp))
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                points.forEachIndexed { i, p ->
                    Text(
                        p.label.take(3),
                        fontSize = 11.sp,
                        color = if (i == sel) ExpColors.Ink else ExpColors.InkSoft,
                        fontWeight = if (i == sel) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
fun SpendLineChart(
    points: List<DailySpend>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val areaTop = ExpColors.Accent.copy(alpha = 0.28f)
    val lineColor = ExpColors.Accent
    val dotRing = ExpColors.Cream
    Canvas(
        modifier.pointerInput(points.size) {
            detectTapGestures { offset ->
                if (points.size > 1) {
                    val step = size.width / (points.size - 1)
                    onSelect((offset.x / step).roundToInt().coerceIn(0, points.lastIndex))
                }
            }
        },
    ) {
        if (points.size < 2) return@Canvas
        val maxV = points.maxOf { it.amount }.coerceAtLeast(1.0).toFloat()
        val stepX = size.width / (points.size - 1)
        val pad = size.height * 0.08f
        val pts = points.mapIndexed { i, p ->
            Offset(i * stepX, size.height - pad - (p.amount.toFloat() / maxV) * (size.height - 2 * pad))
        }
        val line = Path().apply {
            moveTo(pts.first().x, pts.first().y)
            for (i in 1 until pts.size) {
                val prev = pts[i - 1]
                val cur = pts[i]
                val cx = (prev.x + cur.x) / 2f
                cubicTo(cx, prev.y, cx, cur.y, cur.x, cur.y)
            }
        }
        val fill = Path().apply {
            addPath(line)
            lineTo(pts.last().x, size.height)
            lineTo(pts.first().x, size.height)
            close()
        }
        drawPath(fill, Brush.verticalGradient(listOf(areaTop, Color.Transparent)))
        drawPath(line, lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

        val s = pts[selectedIndex.coerceIn(0, pts.lastIndex)]
        drawLine(lineColor.copy(alpha = 0.25f), Offset(s.x, 0f), Offset(s.x, size.height), 1.dp.toPx())
        drawCircle(dotRing, radius = 7.dp.toPx(), center = s)
        drawCircle(lineColor, radius = 4.5.dp.toPx(), center = s)
    }
}

@Composable
fun CategoryBreakdownCard(totals: List<CategoryTotal>, modifier: Modifier = Modifier) {
    val sum = totals.sumOf { it.amount }
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation(),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("Category Breakdown", color = ExpColors.Ink, fontSize = 17.sp, fontFamily = ExpSerif)
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    ExpenseDonut(totals, Modifier.size(140.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Spend", color = ExpColors.InkSoft, fontSize = 11.sp)
                        Text(sum.asInr(), color = ExpColors.Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(20.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    totals.forEach { t ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(t.category.color))
                            Spacer(Modifier.width(8.dp))
                            Text(t.category.label, color = ExpColors.Ink, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            val pct = if (sum > 0) (t.amount / sum * 100).roundToInt() else 0
                            Text("$pct%", color = ExpColors.InkSoft, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseDonut(totals: List<CategoryTotal>, modifier: Modifier = Modifier) {
    val track = ExpColors.Outline
    val colors = totals.map { it.category.color }
    Canvas(modifier) {
        val stroke = 18.dp.toPx()
        val inset = stroke / 2f
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val total = totals.sumOf { it.amount }.toFloat()
        if (total <= 0f) {
            drawArc(track, 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(stroke))
            return@Canvas
        }
        val gap = 8f
        var start = -90f
        totals.forEachIndexed { i, t ->
            val sweep = t.amount.toFloat() / total * 360f
            drawArc(
                color = colors[i],
                startAngle = start + gap / 2f,
                sweepAngle = (sweep - gap).coerceAtLeast(1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
            start += sweep
        }
    }
}

@Composable
fun SectionTitle(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = ExpColors.Ink, fontSize = 18.sp, fontFamily = ExpSerif, modifier = Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Text(actionLabel, color = ExpColors.InkSoft, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onAction))
        }
    }
}

@Composable
fun TransactionRow(item: ExpenseItem, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(ExpColors.Sand),
            contentAlignment = Alignment.Center,
        ) { Icon(item.category.icon, null, tint = item.category.color) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.payee, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${item.category.label} • ${item.dateTime.format(timeFmt)}", color = ExpColors.InkSoft, fontSize = 12.sp)
        }
        Text("-" + item.amount.asInr(), color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
    }
}

/* ============================================================================================
 *  HISTORY
 * ========================================================================================== */

@Composable
fun HistoryScreen(expenses: List<ExpenseItem>, modifier: Modifier = Modifier) {
    val grouped = remember(expenses) {
        expenses.sortedByDescending { it.dateTime }.groupBy { it.dateTime.toLocalDate() }.toList()
    }
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("Expense History", color = ExpColors.Ink, fontSize = 28.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold) }
        grouped.forEach { (date, items) ->
            item(key = "h-$date") {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(dayHeader(date), color = ExpColors.InkSoft, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text("-" + items.sumOf { it.amount }.asInr(), color = ExpColors.InkSoft, fontSize = 13.sp)
                }
            }
            items(items, key = { it.id }) { TransactionRow(it) }
        }
    }
}

/* ============================================================================================
 *  OFFERS  (Best Offers: All / Cards / UPI / Wallets / Coupons)
 * ========================================================================================== */

@Composable
fun OffersScreen(offers: List<DailyOffer>, onApply: (DailyOffer) -> Unit, modifier: Modifier = Modifier) {
    var filter by rememberSaveable { mutableStateOf<OfferType?>(null) }
    val filters = listOf<OfferType?>(null) + OfferType.entries
    val visible = remember(offers, filter) { if (filter == null) offers else offers.filter { it.type == filter } }

    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column {
                Text("Best Offers", color = ExpColors.Ink, fontSize = 28.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
                Text("Refreshed daily", color = ExpColors.InkSoft, fontSize = 13.sp)
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    FilterChip(
                        selected = filter == f,
                        onClick = { filter = f },
                        label = { Text(f?.label ?: "All") },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ExpColors.SandLight,
                            labelColor = ExpColors.Ink,
                            selectedContainerColor = ExpColors.Butter,
                            selectedLabelColor = ExpColors.Green,
                        ),
                    )
                }
            }
        }
        if (visible.isEmpty()) {
            item { Text("No offers here yet.", color = ExpColors.InkSoft, fontSize = 13.sp) }
        }
        items(visible, key = { it.id }) { OfferCard(it, onApply) }
        item {
            OutlinedButton(
                onClick = { /* TODO load more */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ExpColors.Accent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpColors.Accent),
            ) { Text("View More Offers") }
        }
    }
}

@Composable
fun OfferCard(offer: DailyOffer, onApply: (DailyOffer) -> Unit, modifier: Modifier = Modifier) {
    val meta = buildList {
        if (offer.minSpend > 0) add("Min. spend ${offer.minSpend.asInr()}")
        add("Valid till ${offer.validTill.format(shortDateFmt)}")
    }.joinToString(" • ")

    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(offer.brand),
                contentAlignment = Alignment.Center,
            ) { Text(offer.provider.take(1), color = ExpColors.OnOlive, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(offer.provider, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
                Text(offer.title, color = ExpColors.Ink, fontSize = 13.sp)
                Text(meta, color = ExpColors.InkSoft, fontSize = 11.sp)
                if (offer.code != null) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, ExpColors.Gold, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Code: ${offer.code}", color = ExpColors.Accent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Outlined.ContentCopy, null, tint = ExpColors.Accent, modifier = Modifier.size(12.dp))
                    }
                }
            }
            Spacer(Modifier.width(10.dp))
            Button(
                onClick = { onApply(offer) },
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExpColors.Butter, contentColor = ExpColors.Green),
            ) { Text("Apply", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}

/* ============================================================================================
 *  ACCOUNT
 * ========================================================================================== */

private data class AccountRow(val label: String, val icon: ImageVector, val subtitle: String? = null, val onClick: () -> Unit = {})

@Composable
fun AccountScreen(
    state: DashboardUiState,
    onEditLimit: () -> Unit,
    onOpenCoach: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val rows = listOf(
        AccountRow("Linked Payment Apps", Icons.Outlined.Payments, "Google Pay, PhonePe, Paytm"),
        AccountRow("Savings Goals", Icons.Outlined.Savings, "${state.savingsGoalCount} active goals"),
        AccountRow("AI Money Coach", Icons.Outlined.AutoAwesome, "Ask about your spending", onOpenCoach),
        AccountRow("Budget Settings", Icons.Outlined.AccountBalanceWallet, "Limit ${state.budget.limit.asInr()}", onEditLimit),
        AccountRow("Notifications", Icons.Outlined.Notifications),
        AccountRow("Privacy & Security", Icons.Outlined.Lock),
        AccountRow("Help & Support", Icons.Outlined.Info),
        AccountRow("Settings", Icons.Outlined.Settings),
    )
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(60.dp).clip(CircleShape).background(ExpColors.Butter),
                    contentAlignment = Alignment.Center,
                ) { Text(state.userName.take(1).uppercase(), color = ExpColors.Green, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(state.userName, color = ExpColors.Ink, fontSize = 24.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
                    Text(state.email, color = ExpColors.InkSoft, fontSize = 13.sp)
                }
            }
        }
        item { HealthScoreCard(state.healthScore) }
        item { AppearanceCard(state.themeMode, onThemeModeChange) }
        item {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation(),
            ) {
                Column {
                    rows.forEach { row ->
                        Row(
                            Modifier.fillMaxWidth().clickable(onClick = row.onClick).padding(horizontal = 18.dp, vertical = 15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(row.icon, null, tint = ExpColors.Accent)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(row.label, color = ExpColors.Ink)
                                if (row.subtitle != null) Text(row.subtitle, color = ExpColors.InkSoft, fontSize = 11.sp)
                            }
                            Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, tint = ExpColors.InkSoft, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppearanceCard(mode: ThemeMode, onChange: (ThemeMode) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation(),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Appearance", color = ExpColors.Ink, fontSize = 17.sp, fontFamily = ExpSerif)
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(ExpColors.SandLight).padding(4.dp),
            ) {
                ThemeMode.entries.forEach { m ->
                    val sel = m == mode
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(11.dp))
                            .background(if (sel) ExpColors.Butter else Color.Transparent)
                            .clickable { onChange(m) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            m.label,
                            fontSize = 13.sp,
                            fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (sel) ExpColors.Green else ExpColors.InkSoft,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HealthScoreCard(score: Int, modifier: Modifier = Modifier) {
    val label = when {
        score >= 75 -> "Good"
        score >= 50 -> "Fair"
        else -> "Needs work"
    }
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ExpColors.Olive), elevation = expCardElevation(),
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            val ringTrack = ExpColors.OnOlive.copy(alpha = 0.2f)
            val ringFill = HeroGold
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(78.dp)) {
                    val sw = 8.dp.toPx()
                    val arc = Size(size.width - sw, size.height - sw)
                    drawArc(ringTrack, 0f, 360f, false, Offset(sw / 2, sw / 2), arc, style = Stroke(sw))
                    drawArc(
                        ringFill, -90f, 360f * score.coerceIn(0, 100) / 100f, false,
                        Offset(sw / 2, sw / 2), arc, style = Stroke(sw, cap = StrokeCap.Round),
                    )
                }
                Text("$score", color = ExpColors.OnOlive, fontSize = 22.sp, fontFamily = ExpSerif, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text("Financial Health Score", color = ExpColors.OnOlive.copy(alpha = 0.75f), fontSize = 12.sp)
                Text(label, color = ExpColors.OnOlive, fontSize = 20.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
                Text("Out of 100", color = ExpColors.OnOlive.copy(alpha = 0.75f), fontSize = 12.sp)
            }
        }
    }
}

/* ============================================================================================
 *  FLOW SCREENS: Scan QR -> Add Expense -> Review & Pay -> Success
 * ========================================================================================== */

@Composable
private fun RouteScaffold(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxSize().background(ExpColors.Cream).navigationBarsPadding(),
        content = content,
    )
}

@Composable
private fun ScreenTopBar(title: String, onBack: () -> Unit, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().background(ExpColors.Olive).statusBarsPadding().padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = ExpColors.OnOlive) }
        Text(title, fontFamily = ExpSerif, fontSize = 20.sp, color = ExpColors.OnOlive, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ExpColors.Butter,
            contentColor = ExpColors.Green,
            disabledContainerColor = ExpColors.Outline,
            disabledContentColor = ExpColors.InkSoft,
        ),
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ExpColors.Accent,
    unfocusedBorderColor = ExpColors.Outline,
    focusedLabelColor = ExpColors.Accent,
    cursorColor = ExpColors.Accent,
    focusedContainerColor = ExpColors.SandLight,
    unfocusedContainerColor = ExpColors.SandLight,
)

/* ---- Scan QR ---- */

@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onScanned: (String) -> Unit,
    onEnterManually: () -> Unit,
    onPickContact: () -> Unit,
    modifier: Modifier = Modifier,
    scanner: @Composable (onResult: (String) -> Unit) -> Unit = { SimulatedScanner(it) },
) {
    RouteScaffold(modifier) {
        ScreenTopBar("Scan QR", onBack)
        Box(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(24.dp)),
        ) {
            scanner(onScanned)
            ScanFrameOverlay(Modifier.align(Alignment.Center).size(230.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Scan any UPI QR to pay",
            color = ExpColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Or enter UPI ID",
            color = ExpColors.InkSoft,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onEnterManually).padding(8.dp),
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onEnterManually,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExpColors.Butter, contentColor = ExpColors.Green),
            ) { Text("Enter Amount\nManually", fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 15.sp) }
            OutlinedButton(
                onClick = onPickContact,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ExpColors.Accent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpColors.Accent),
            ) {
                Icon(Icons.Outlined.Contacts, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pay a Contact", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ScanFrameOverlay(modifier: Modifier = Modifier) {
    val c = ExpColors.OnOlive
    Canvas(modifier) {
        val len = 36.dp.toPx()
        val sw = 5.dp.toPx()
        fun corner(x: Float, y: Float, dx: Float, dy: Float) {
            drawLine(c, Offset(x, y), Offset(x + dx * len, y), sw, StrokeCap.Round)
            drawLine(c, Offset(x, y), Offset(x, y + dy * len), sw, StrokeCap.Round)
        }
        corner(0f, 0f, 1f, 1f)
        corner(size.width, 0f, -1f, 1f)
        corner(0f, size.height, 1f, -1f)
        corner(size.width, size.height, -1f, -1f)
    }
}

/** Stand-in for the camera. Replace with CameraX PreviewView + ML Kit barcode scanning via AndroidView. */
@Composable
fun SimulatedScanner(onResult: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(ExpColors.OliveDark)
            .clickable { onResult("upi://pay?pa=sagarmess@okhdfc&pn=Sagar%20Mess") },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.QrCodeScanner, null, tint = ExpColors.OnOlive.copy(alpha = 0.3f), modifier = Modifier.size(96.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                "Camera preview goes here\n(tap to simulate a scan)",
                color = ExpColors.OnOlive.copy(alpha = 0.6f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/* ---- Add Expense ---- */

@Composable
fun AddExpenseScreen(
    initialPayee: String,
    onBack: () -> Unit,
    onNext: (ExpenseDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amountText by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(ExpenseCategory.FOOD) }
    var payee by rememberSaveable { mutableStateOf(initialPayee) }
    var note by rememberSaveable { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    val canNext = amount != null && amount > 0 && payee.isNotBlank()

    RouteScaffold(modifier) {
        ScreenTopBar("Add Expense", onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            OutlinedTextField(
                value = amountText,
                onValueChange = { v -> if (v.matches(Regex("""\d{0,7}(\.\d{0,2})?"""))) amountText = v },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 32.sp, fontFamily = ExpSerif, color = ExpColors.Ink),
                prefix = { Text("₹ ", fontSize = 32.sp, fontFamily = ExpSerif, color = ExpColors.Ink) },
                placeholder = { Text("0", fontSize = 32.sp, fontFamily = ExpSerif, color = ExpColors.InkSoft) },
                trailingIcon = if (amountText.isNotEmpty()) {
                    { IconButton(onClick = { amountText = "" }) { Icon(Icons.Outlined.Close, "Clear", tint = ExpColors.InkSoft) } }
                } else null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors(),
            )

            Text("Select Category", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 16.sp)
            ExpenseCategory.entries.chunked(4).forEach { rowCats ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowCats.forEach { c ->
                        CategoryTile(c, selected = c == category, onClick = { category = c }, modifier = Modifier.weight(1f))
                    }
                    repeat(4 - rowCats.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            OutlinedTextField(
                value = payee,
                onValueChange = { payee = it },
                label = { Text("Contact / Merchant") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Add Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(4.dp))
        }
        PrimaryButton(
            "Next",
            onClick = { onNext(ExpenseDraft(amount ?: 0.0, category, payee.trim(), note.trim())) },
            enabled = canNext,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun CategoryTile(category: ExpenseCategory, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ExpColors.SandLight else ExpColors.Sand)
            .border(if (selected) 2.dp else 1.dp, if (selected) ExpColors.Accent else ExpColors.Outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(category.icon, null, tint = if (selected) ExpColors.Accent else ExpColors.InkSoft)
        Text(
            category.label,
            fontSize = 10.sp,
            color = ExpColors.Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/* ---- Review & Pay ---- */

@Composable
fun ReviewPayScreen(
    draft: ExpenseDraft,
    budget: BudgetState,
    offers: List<DailyOffer>,
    onBack: () -> Unit,
    onOverrideChange: (Boolean) -> Unit,
    onProceed: (payable: Double, app: UpiApp) -> Unit,
    onLogOnly: (payable: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bestOffer = remember(draft.amount, offers) {
        offers.filter { it.type != OfferType.COUPON && it.savingsOn(draft.amount) > 0 }
            .maxByOrNull { it.savingsOn(draft.amount) }
    }
    val savings = bestOffer?.savingsOn(draft.amount) ?: 0.0
    val payable = draft.amount - savings
    val gate = budget.evaluate(payable)
    val blocked = gate is ExpenseGate.Blocked
    var app by remember { mutableStateOf(UpiApp.GPAY) }

    RouteScaffold(modifier) {
        ScreenTopBar("Review & Pay", onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Merchant
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(CircleShape).background(ExpColors.Cream), contentAlignment = Alignment.Center) {
                        Icon(draft.category.icon, null, tint = draft.category.color)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(draft.payee, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (draft.note.isBlank()) draft.category.label else "${draft.category.label} • ${draft.note}",
                            color = ExpColors.InkSoft,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            // Amount
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Column(Modifier.padding(16.dp)) {
                    Text(draft.amount.asInr(), color = ExpColors.Ink, fontSize = 34.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
                    Text("Total Amount", color = ExpColors.InkSoft, fontSize = 12.sp)
                }
            }

            // Applied offer
            if (bestOffer != null) {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ExpColors.Gold.copy(alpha = 0.14f)),
                    elevation = expCardElevation(),
                    border = BorderStroke(1.5.dp, ExpColors.Gold),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(bestOffer.brand), contentAlignment = Alignment.Center) {
                            Text(bestOffer.provider.take(1), color = ExpColors.OnOlive, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(bestOffer.title, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${bestOffer.provider} • You save ${savings.asInr()}", color = ExpColors.InkSoft, fontSize = 12.sp)
                        }
                        Text(
                            "Verified",
                            color = ExpColors.OnGold,
                            fontSize = 10.sp,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ExpColors.Gold).padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
            }

            // Final price
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Estimated Final Price", color = ExpColors.Ink, modifier = Modifier.weight(1f))
                Text(payable.asInr(), color = ExpColors.Ink, fontSize = 20.sp, fontFamily = ExpSerif, fontWeight = FontWeight.Bold)
            }

            // Budget impact
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Monthly Budget Impact", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 15.sp)
                    Row {
                        Text("${draft.category.label} • ${payable.asInr()}", color = ExpColors.InkSoft, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    }
                    Row {
                        Text("Remaining Balance", color = ExpColors.Ink, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text((budget.remaining - payable).asSignedInr(), color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            when (gate) {
                is ExpenseGate.Blocked -> {
                    StatusBanner(Icons.Outlined.Block, gate.message, ExpColors.Danger)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Override monthly limit", color = ExpColors.Ink, modifier = Modifier.weight(1f))
                        Switch(
                            checked = budget.overrideEnabled,
                            onCheckedChange = onOverrideChange,
                            colors = SwitchDefaults.colors(checkedTrackColor = ExpColors.Danger, checkedThumbColor = ExpColors.Cream),
                        )
                    }
                }
                is ExpenseGate.AllowedWithWarning -> StatusBanner(Icons.Outlined.Warning, gate.message, ExpColors.Warning)
                ExpenseGate.Allowed -> Unit
            }

            // UPI app
            Text("Pay with", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 16.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                UpiApp.entries.forEach { u ->
                    val sel = u == app
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (sel) ExpColors.SandLight else ExpColors.Sand)
                            .border(if (sel) 2.dp else 1.dp, if (sel) ExpColors.Accent else ExpColors.Outline, RoundedCornerShape(16.dp))
                            .clickable { app = u }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(Modifier.size(34.dp).clip(CircleShape).background(ExpColors.Butter), contentAlignment = Alignment.Center) {
                            Text(u.label.take(1), color = ExpColors.Green, fontWeight = FontWeight.Bold)
                        }
                        Text(u.label.substringBefore(" "), fontSize = 10.sp, color = ExpColors.Ink, maxLines = 1)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            PrimaryButton("Proceed to Pay", onClick = { onProceed(payable, app) }, enabled = !blocked)
            TextButton(
                onClick = { onLogOnly(payable) },
                enabled = !blocked,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Log only (already paid)", color = if (blocked) ExpColors.InkSoft else ExpColors.Accent) }
        }
    }
}

/* ---- Payment Success ---- */

@Composable
fun PaymentSuccessScreen(
    draft: ExpenseDraft,
    paidWith: UpiApp,
    reference: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    RouteScaffold(modifier) {
        Column(
            Modifier.weight(1f).fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(96.dp).clip(CircleShape).background(ExpColors.Butter), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CheckCircle, null, tint = ExpColors.Green, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Payment Successful!", color = ExpColors.Ink, fontSize = 24.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(draft.amount.asInr(), color = ExpColors.Ink, fontSize = 40.sp, fontFamily = ExpSerif, fontWeight = FontWeight.Bold)
            Text(draft.payee, color = ExpColors.Ink, fontSize = 16.sp)
            Text("${draft.dateTime.format(fullFmt)} • ${paidWith.label}", color = ExpColors.InkSoft, fontSize = 12.sp)
            Spacer(Modifier.height(24.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("UPI Ref. No.", color = ExpColors.InkSoft, fontSize = 11.sp)
                        Text(reference, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { clipboard.setText(AnnotatedString(reference)) }) {
                        Icon(Icons.Outlined.ContentCopy, "Copy reference", tint = ExpColors.Accent)
                    }
                }
            }
        }
        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryButton("Back to Home", onClick = onDone)
            OutlinedButton(
                onClick = { /* TODO receipt */ },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ExpColors.Accent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpColors.Accent),
            ) { Text("View Receipt") }
        }
    }
}

/* ---- AI Money Coach ---- */

@Composable
fun CoachScreen(
    messages: List<CoachMessage>,
    onBack: () -> Unit,
    onAsk: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember { mutableStateOf("") }
    val suggestions = listOf("How much did I spend on food this week?", "Give me saving tips", "Show my budget status")

    RouteScaffold(modifier) {
        ScreenTopBar("AI Money Coach", onBack) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = HeroGold, modifier = Modifier.padding(end = 12.dp))
        }
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(messages) { m ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.fromUser) Arrangement.End else Arrangement.Start) {
                    Text(
                        m.text,
                        color = if (m.fromUser) ExpColors.Green else ExpColors.Ink,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (m.fromUser) ExpColors.Butter else ExpColors.Sand)
                            .padding(14.dp),
                    )
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestions) { s ->
                Text(
                    s,
                    fontSize = 12.sp,
                    color = ExpColors.Accent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, ExpColors.Accent, RoundedCornerShape(50))
                        .clickable { onAsk(s) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Ask me anything...") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50),
                colors = fieldColors(),
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { if (input.isNotBlank()) { onAsk(input.trim()); input = "" } },
                modifier = Modifier.size(48.dp).clip(CircleShape).background(ExpColors.Butter),
            ) { Icon(Icons.AutoMirrored.Outlined.Send, "Send", tint = ExpColors.Green) }
        }
    }
}

/* ---- Edit limit ---- */

@Composable
fun EditLimitDialog(currentLimit: Double, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var text by remember { mutableStateOf(if (currentLimit > 0) currentLimit.roundToInt().toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExpColors.SandLight,
        title = { Text("Monthly limit", fontFamily = ExpSerif) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { v -> if (v.all(Char::isDigit) && v.length <= 8) text = v },
                prefix = { Text("₹ ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
            )
        },
        confirmButton = {
            TextButton(onClick = { text.toDoubleOrNull()?.let(onConfirm) }, enabled = text.toDoubleOrNull() != null) { Text("Set") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

/* ============================================================================================
 *  SAMPLE DATA + PREVIEWS
 * ========================================================================================== */

object SampleData {
    fun expenses(): List<ExpenseItem> {
        val now = LocalDateTime.now()
        return listOf(
            ExpenseItem(1, "Sagar Mess", 120.0, ExpenseCategory.FOOD, now.minusHours(2)),
            ExpenseItem(2, "PMPML Pass", 600.0, ExpenseCategory.TRAVEL, now.minusHours(5)),
            ExpenseItem(3, "Amazon", 2499.0, ExpenseCategory.SHOPPING, now.minusDays(1).minusHours(1)),
            ExpenseItem(4, "Hostel Rent", 8200.0, ExpenseCategory.BILLS, now.minusDays(1).minusHours(6)),
            ExpenseItem(5, "Rohan Kulkarni", 350.0, ExpenseCategory.OTHER, now.minusDays(2)),
            ExpenseItem(6, "PVR Cinemas", 540.0, ExpenseCategory.ENTERTAINMENT, now.minusDays(3)),
            ExpenseItem(7, "Vaishali", 280.0, ExpenseCategory.FOOD, now.minusDays(3).minusHours(4)),
        )
    }

    fun offers(): List<DailyOffer> {
        val today = LocalDate.now()
        return listOf(
            DailyOffer("o1", "HDFC Bank", "10% OFF up to ₹45", "Credit card offer", OfferType.CARD, today.plusDays(6),
                minSpend = 300.0, percentOff = 10.0, maxOff = 45.0, brand = Color(0xFF00796B)),
            DailyOffer("o2", "Amazon Pay", "₹30 Cashback", "Wallet cashback", OfferType.WALLET, today.plusDays(6),
                minSpend = 200.0, flatOff = 30.0, brand = Color(0xFFA8743F)),
            DailyOffer("o3", "PhonePe", "5% Cashback", "UPI cashback", OfferType.UPI, today.plusDays(4),
                minSpend = 400.0, percentOff = 5.0, maxOff = 100.0, brand = Color(0xFF4A7C59)),
            DailyOffer("o4", "Zomato", "40% off up to ₹80", "Placeholder for scraped deal", OfferType.COUPON, today.plusDays(2),
                minSpend = 199.0, code = "ZOMATO40", brand = Color(0xFF1E4D2B)),
            DailyOffer("o5", "Domino's", "Buy 1 Get 1 on Medium Pizzas", "Placeholder for scraped deal", OfferType.COUPON, today.plusDays(5),
                code = "BOGO24", brand = Color(0xFF8C6A1F)),
        )
    }

    fun state(spent: Double = 12589.0, limit: Double = 15000.0, override: Boolean = false) = DashboardUiState(
        userName = "Naved",
        email = "naved@example.com",
        greeting = "Good Morning",
        budget = BudgetState(spent = spent, limit = limit, overrideEnabled = override),
        expenses = expenses(),
        weeklySpend = listOf(
            DailySpend("Sun", 320.0), DailySpend("Mon", 540.0), DailySpend("Tue", 1200.0),
            DailySpend("Wed", 450.0), DailySpend("Thu", 2499.0), DailySpend("Fri", 700.0), DailySpend("Sat", 980.0),
        ),
        offers = offers(),
        healthScore = 78,
        savingsGoalCount = 2,
        coachMessages = listOf(
            CoachMessage("How much did I spend on food this week?", fromUser = true),
            CoachMessage("You spent ₹2,400 on food this week, which is 25% more than last week.", fromUser = false),
        ),
    )
}

/** Stateful host faking a ViewModel so Interactive Preview works end to end. */
@Composable
fun ExptrackedPreviewHost() {
    var state by remember { mutableStateOf(SampleData.state()) }
    ExptrackedTheme(state.themeMode) {
    ExptrackedApp(
        state = state,
        actions = ExptrackedActions(
            onSetThemeMode = { state = state.copy(themeMode = it) },
            onSaveExpense = { d ->
                if (state.budget.evaluate(d.amount) !is ExpenseGate.Blocked) {
                    val item = ExpenseItem(System.nanoTime(), d.payee, d.amount, d.category, d.dateTime)
                    state = state.copy(
                        expenses = listOf(item) + state.expenses,
                        budget = state.budget.copy(spent = state.budget.spent + d.amount),
                    )
                }
            },
            onSetOverride = { state = state.copy(budget = state.budget.copy(overrideEnabled = it)) },
            onSetLimit = { state = state.copy(budget = state.budget.copy(limit = it)) },
            onPickContact = { state = state.copy(pendingPayee = "Rohan Kulkarni") },
            onPayeeConsumed = { state = state.copy(pendingPayee = "") },
            onAskCoach = { q ->
                state = state.copy(
                    coachMessages = state.coachMessages +
                        CoachMessage(q, true) +
                        CoachMessage("Placeholder reply. Wire this to your coach backend.", false),
                )
            },
        ),
    )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun HomePreview() = ExptrackedTheme { ExptrackedApp(SampleData.state()) }

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun InteractivePreview() = ExptrackedTheme { ExptrackedPreviewHost() }

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun BudgetWarningPreview() = ExptrackedTheme {
    Box(Modifier.padding(16.dp)) { BudgetCard(BudgetState(12589.0, 15000.0), {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
private fun BudgetExceededPreview() = ExptrackedTheme {
    Box(Modifier.padding(16.dp)) { BudgetCard(BudgetState(16200.0, 15000.0), {}, {}) }
}

@Preview(showBackground = true, backgroundColor = 0xFF212121, heightDp = 800)
@Composable
private fun HistoryPreview() = ExptrackedTheme { HistoryScreen(SampleData.expenses()) }

@Preview(showBackground = true, backgroundColor = 0xFF212121, heightDp = 800)
@Composable
private fun OffersPreview() = ExptrackedTheme { OffersScreen(SampleData.offers(), {}) }

@Preview(showBackground = true, backgroundColor = 0xFF212121, heightDp = 800)
@Composable
private fun AccountPreview() = ExptrackedTheme { AccountScreen(SampleData.state(), {}, {}) }

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ScanPreview() = ExptrackedTheme { ScanQrScreen({}, {}, {}, {}) }

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AddExpensePreview() = ExptrackedTheme { AddExpenseScreen("Sagar Mess", {}, {}) }

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ReviewPayPreview() = ExptrackedTheme {
    ReviewPayScreen(
        draft = ExpenseDraft(450.0, ExpenseCategory.FOOD, "Sagar Mess", "Lunch at Sagar Mess"),
        budget = BudgetState(12589.0, 15000.0),
        offers = SampleData.offers(),
        onBack = {}, onOverrideChange = {}, onProceed = { _, _ -> }, onLogOnly = {},
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ReviewPayBlockedPreview() = ExptrackedTheme {
    ReviewPayScreen(
        draft = ExpenseDraft(450.0, ExpenseCategory.FOOD, "Sagar Mess"),
        budget = BudgetState(14900.0, 15000.0),
        offers = SampleData.offers(),
        onBack = {}, onOverrideChange = {}, onProceed = { _, _ -> }, onLogOnly = {},
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun SuccessPreview() = ExptrackedTheme {
    PaymentSuccessScreen(ExpenseDraft(405.0, ExpenseCategory.FOOD, "Sagar Mess"), UpiApp.GPAY, "123456789012", {})
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun CoachPreview() = ExptrackedTheme { CoachScreen(SampleData.state().coachMessages, {}, {}) }

/* ---- Light theme previews ---- */

@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA, heightDp = 900)
@Composable
private fun HomeLightPreview() = ExptrackedTheme(ThemeMode.LIGHT) { ExptrackedApp(SampleData.state()) }

@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA, heightDp = 800)
@Composable
private fun AccountLightPreview() = ExptrackedTheme(ThemeMode.LIGHT) { AccountScreen(SampleData.state(), {}, {}) }

@Preview(showBackground = true, backgroundColor = 0xFFFAFAFA, heightDp = 800)
@Composable
private fun OffersLightPreview() = ExptrackedTheme(ThemeMode.LIGHT) { OffersScreen(SampleData.offers(), {}) }
