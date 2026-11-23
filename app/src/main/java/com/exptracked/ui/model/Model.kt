package com.exptracked.ui.model

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.exptracked.ui.theme.ExpColors
import com.exptracked.ui.theme.ExpSerif
import com.exptracked.ui.theme.LocalExpPalette
import com.exptracked.ui.theme.ThemeMode
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

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
    val pendingConfirmation: Boolean = false,
)

data class ExpenseDraft(
    val amount: Double,
    val category: ExpenseCategory,
    val payee: String,
    val note: String = "",
    val vpa: String = "",
    val tr: String = "",
    val mc: String = "",
    val rawUri: String = "",
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

enum class UpiApp(val label: String, val packageName: String? = null) {
    GPAY("Google Pay", "com.google.android.apps.nbu.paisa.user"),
    PHONEPE("PhonePe", "com.phonepe.app"),
    PAYTM("Paytm", "net.one97.paytm"),
    OTHER("Other UPI App", null),
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
    val preferredUpiPackage: String? = null,
    /** Expense draft awaiting confirmation after returning from a UPI app. */
    val pendingExpense: ExpenseDraft? = null,
    /** Whether to show the post-payment confirmation dialog. */
    val showPaymentConfirm: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.DARK,
) {
    val categoryTotals: List<CategoryTotal>
        get() = expenses.groupBy { it.category }
            .map { (cat, items) -> CategoryTotal(cat, items.sumOf { it.amount }) }
            .sortedByDescending { it.amount }
}

data class ExptrackedActions(
    val onPickContact: () -> Unit = {},
    /** Log the expense as pending and launch the UPI app home. */
    val onPayAndLog: (ExpenseDraft) -> Unit = { _ -> },
    /** Persist the expense (Room). Called after pay, or from "Log only". */
    val onSaveExpense: (ExpenseDraft) -> Unit = {},
    val onSetOverride: (Boolean) -> Unit = {},
    val onSetLimit: (Double) -> Unit = {},
    val onApplyOffer: (DailyOffer) -> Unit = {},
    val onAskCoach: (String) -> Unit = {},
    val onPayeeConsumed: () -> Unit = {},
    /** Persist the choice (DataStore) and feed it back via DashboardUiState.themeMode. */
    val onSetThemeMode: (ThemeMode) -> Unit = {},
    val onSetUserName: (String) -> Unit = {},
    val onSetPreferredUpiApp: (String?) -> Unit = {},
    val onSetPendingExpense: (ExpenseDraft) -> Unit = {},
    val onConfirmPendingExpense: () -> Unit = {},
    val onDiscardPendingExpense: () -> Unit = {},
    val onAppResumed: () -> Unit = {},
    val onConfirmPending: () -> Unit = {},
    val onDiscardPending: () -> Unit = {},
)

/* ============================================================================================
 *  FORMATTING HELPERS
 * ========================================================================================== */

internal val IN_LOCALE: Locale = Locale.forLanguageTag("en-IN")
internal val timeFmt = DateTimeFormatter.ofPattern("hh:mm a", IN_LOCALE)
internal val dateFmt = DateTimeFormatter.ofPattern("EEE, d MMM", IN_LOCALE)
internal val shortDateFmt = DateTimeFormatter.ofPattern("d MMM", IN_LOCALE)
internal val fullFmt = DateTimeFormatter.ofPattern("dd MMM yyyy • hh:mm a", IN_LOCALE)

fun Double.asInr(): String {
    val nf = NumberFormat.getNumberInstance(IN_LOCALE).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    return "₹" + nf.format(this)
}

internal fun Double.asSignedInr(): String = if (this < 0) "-" + (-this).asInr() else asInr()

internal fun dayHeader(date: LocalDate): String {
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
    if (uri.scheme != "upi" || uri.host != "pay") return@runCatching raw
    uri.getQueryParameter("pn")?.takeIf { it.isNotBlank() } ?: uri.getQueryParameter("pa") ?: raw
}.getOrDefault(raw)

/** Pulls the payment address (VPA) out of a upi://pay?pa=... QR payload. */
fun parsePayeeVpa(raw: String): String = runCatching {
    val uri = Uri.parse(raw)
    if (uri.scheme != "upi" || uri.host != "pay") return@runCatching ""
    uri.getQueryParameter("pa")?.takeIf { it.isNotBlank() } ?: ""
}.getOrDefault("")

/** Extracts the amount (am) from a upi://pay?... QR payload. */
fun parseAmount(raw: String): String = runCatching {
    val uri = Uri.parse(raw)
    if (uri.scheme != "upi" || uri.host != "pay") return@runCatching ""
    uri.getQueryParameter("am")?.takeIf { it.isNotBlank() } ?: ""
}.getOrDefault("")

/** Extracts the transaction reference (tr) from a upi://pay?... QR payload. */
fun parseTr(raw: String): String = runCatching {
    val uri = Uri.parse(raw)
    if (uri.scheme != "upi" || uri.host != "pay") return@runCatching ""
    uri.getQueryParameter("tr")?.takeIf { it.isNotBlank() } ?: ""
}.getOrDefault("")

/** Extracts the merchant category code (mc) from a upi://pay?... QR payload. */
fun parseMc(raw: String): String = runCatching {
    val uri = Uri.parse(raw)
    if (uri.scheme != "upi" || uri.host != "pay") return@runCatching ""
    uri.getQueryParameter("mc")?.takeIf { it.isNotBlank() } ?: ""
}.getOrDefault("")

/** Gold that reads on the hero surfaces (teal in Light, charcoal in Dark). */
internal val HeroGold = Color(0xFFF0D77A)

/** Status tones for the hero card (readable on both the teal and charcoal hero). */
internal fun BudgetStatus.onDark(): Color = when (this) {
    BudgetStatus.SAFE -> Color(0xFFB9F6CA)
    BudgetStatus.WARNING -> HeroGold
    BudgetStatus.EXCEEDED -> Color(0xFFFFB3BF)
}

internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/** Route screens have a hero-colored top bar (light icons); tab screens follow the page background. */
@Composable
internal fun StatusBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    SideEffect {
        val window = view.context.findActivity()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
    }
}

