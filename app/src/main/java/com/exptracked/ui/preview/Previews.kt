@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.preview

import com.exptracked.ui.*
import com.exptracked.ui.screens.*
import com.exptracked.ui.theme.*
import com.exptracked.ui.components.*
import com.exptracked.ui.model.*
import com.exptracked.ui.components.*

import com.exptracked.ui.model.BudgetState
import com.exptracked.ui.model.BudgetStatus
import com.exptracked.ui.model.CategoryTotal
import com.exptracked.ui.model.CoachMessage
import com.exptracked.ui.model.DailyOffer
import com.exptracked.ui.model.DailySpend
import com.exptracked.ui.model.DashboardUiState
import com.exptracked.ui.model.ExptrackedActions
import com.exptracked.ui.model.ExpenseCategory
import com.exptracked.ui.model.ExpenseDraft
import com.exptracked.ui.model.ExpenseGate
import com.exptracked.ui.model.ExpenseItem
import com.exptracked.ui.model.OfferType
import com.exptracked.ui.model.UpiApp
import com.exptracked.ui.model.evaluate
import com.exptracked.ui.model.asInr
import com.exptracked.ui.model.greetingFor
import com.exptracked.ui.model.parsePayee
import com.exptracked.ui.model.dayHeader
import com.exptracked.ui.model.asSignedInr
import com.exptracked.ui.model.HeroGold
import com.exptracked.ui.model.StatusBarIcons
import com.exptracked.ui.model.onDark
import com.exptracked.ui.model.shortDateFmt
import com.exptracked.ui.model.fullFmt
import com.exptracked.ui.model.timeFmt
import com.exptracked.ui.model.IN_LOCALE
import com.exptracked.ui.model.IN_LOCALE
import com.exptracked.ui.theme.ExpColors
import com.exptracked.ui.theme.ExpSerif
import com.exptracked.ui.theme.ExptrackedTheme
import com.exptracked.ui.theme.LocalExpPalette
import com.exptracked.ui.theme.ThemeMode
import com.exptracked.ui.theme.expCardElevation
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
private fun AddExpensePreview() = ExptrackedTheme { AddExpenseScreen(initialPayee = "Sagar Mess", onBack = {}, onNext = {}) }

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
