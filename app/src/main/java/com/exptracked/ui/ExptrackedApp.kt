@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui
import com.exptracked.ui.screens.HomeScreen
import com.exptracked.ui.screens.HistoryScreen
import com.exptracked.ui.screens.OffersScreen
import com.exptracked.ui.screens.AccountScreen
import com.exptracked.ui.screens.ScanQrScreen
import com.exptracked.ui.screens.AddExpenseScreen
import com.exptracked.ui.screens.QuickEntryScreen
import com.exptracked.ui.screens.CoachScreen
import com.exptracked.ui.components.EditLimitDialog
import com.exptracked.ui.components.EditNameDialog
import com.exptracked.ui.components.CameraPreview
import com.exptracked.ui.model.StatusBarIcons
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
import com.exptracked.ui.model.evaluate
import com.exptracked.ui.model.asInr
import com.exptracked.ui.model.greetingFor
import com.exptracked.ui.model.parsePayee
import com.exptracked.ui.model.parsePayeeVpa
import com.exptracked.ui.model.dayHeader
import com.exptracked.ui.model.asSignedInr
import com.exptracked.ui.model.HeroGold
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
    data class QuickEntry(val payee: String = "", val vpa: String = "") : Route
    data object Coach : Route
}

@Composable
fun ExptrackedApp(
    state: DashboardUiState,
    actions: ExptrackedActions = ExptrackedActions(),
    /** Plug CameraX / ML Kit here and call onResult with the decoded QR text. */
    scanner: @Composable (onResult: (String) -> Unit) -> Unit = { CameraPreview(it) },
) {
    var tab by rememberSaveable { mutableStateOf(AppTab.HOME) }
    var showLimitDialog by rememberSaveable { mutableStateOf(false) }
    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    val stack = remember { mutableStateListOf<Route>() }
    val clipboard = LocalClipboardManager.current
    StatusBarIcons(darkIcons = stack.isEmpty() && !LocalExpPalette.current.isDark)

    LaunchedEffect(state.pendingPayee) {
        if (state.pendingPayee.isNotBlank()) {
            stack.add(Route.QuickEntry(state.pendingPayee))
            actions.onPayeeConsumed()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentState by rememberUpdatedState(state)
    var showPendingConfirmation by remember { mutableStateOf(false) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (currentState.expenses.any { it.pendingConfirmation }) {
                    showPendingConfirmation = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler(enabled = stack.isNotEmpty()) {
        stack.removeAt(stack.lastIndex)
    }

    when (val route = stack.lastOrNull()) {
        null -> Scaffold(
            containerColor = ExpColors.Cream,
            bottomBar = {
                ExpBottomBar(selected = tab, onSelect = { tab = it }, onScan = { stack.add(Route.QuickEntry()) })
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
                        onEditName = { showNameDialog = true },
                        onSetPreferredUpiApp = actions.onSetPreferredUpiApp,
                    )
                }
            }
        }

        Route.Scan -> ScanQrScreen(
            onBack = { stack.removeAt(stack.lastIndex) },
            onScanned = { raw -> stack.add(Route.QuickEntry(parsePayee(raw), parsePayeeVpa(raw))) },
            onEnterManually = { stack.add(Route.QuickEntry()) },
            onPickContact = actions.onPickContact,
            scanner = scanner,
        )

        is Route.QuickEntry -> QuickEntryScreen(
            initialPayee = route.payee,
            initialVpa = route.vpa,
            onBack = { stack.removeAt(stack.lastIndex) },
            onPayAndLog = { draft ->
                actions.onPayAndLog(draft)
                stack.clear()
            },
            onScan = { stack.add(Route.Scan) },
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
            onConfirm = { v -> actions.onSetLimit(v); showLimitDialog = false },
        )
    }

    if (showNameDialog) {
        EditNameDialog(
            currentName = state.userName,
            onDismiss = { showNameDialog = false },
            onConfirm = { v -> actions.onSetUserName(v); showNameDialog = false },
        )
    }

    if (showPendingConfirmation) {
        val pending = state.expenses.firstOrNull { it.pendingConfirmation }
        val amount = pending?.amount ?: 0.0
        val payee = pending?.payee ?: ""
        ModalBottomSheet(
            onDismissRequest = { showPendingConfirmation = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Logged ${amount.asInr()} to $payee — Did the payment go through?", color = ExpColors.Ink, fontFamily = ExpSerif)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { actions.onConfirmPending(); showPendingConfirmation = false },
                        modifier = Modifier.weight(1f),
                    ) { Text("Yes, Keep") }
                    OutlinedButton(
                        onClick = { actions.onDiscardPending(); showPendingConfirmation = false },
                        modifier = Modifier.weight(1f),
                    ) { Text("No, Delete") }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
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
