@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.screens

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
import com.exptracked.ui.components.*
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
import androidx.compose.material.icons.outlined.DateRange
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButtonDefaults
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
 *  ACCOUNT
 * ========================================================================================== */

private data class AccountRow(val label: String, val icon: ImageVector, val subtitle: String? = null, val onClick: () -> Unit = {})

@Composable
fun AccountScreen(
    state: DashboardUiState,
    onEditLimit: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onEditName: () -> Unit = {},
    onChangePin: () -> Unit = {},
    onSetBudgetCycle: (String) -> Unit = {},
    onSetDailyLimit: (Double) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showCycleDialog by rememberSaveable { mutableStateOf(false) }
    var showDailyLimitDialog by rememberSaveable { mutableStateOf(false) }
    val rows = listOf(
        AccountRow("Edit Name", Icons.Outlined.Person, state.userName, onEditName),
        AccountRow("Budget Settings", Icons.Outlined.AccountBalanceWallet, "Limit ${state.budget.limit.asInr()}", onEditLimit),
        AccountRow("Budget Cycle", Icons.Outlined.DateRange, state.budgetCycle.replaceFirstChar { it.uppercase() }, { showCycleDialog = true }),
        AccountRow("Daily Limit", Icons.Outlined.Edit, if (state.budget.dailyLimit > 0) state.budget.dailyLimit.asInr() else "Not set", { showDailyLimitDialog = true }),
        AccountRow("Change PIN", Icons.Outlined.Lock, "Protect budget edits", onChangePin),
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

    if (showCycleDialog) {
        AlertDialog(
            onDismissRequest = { showCycleDialog = false },
            containerColor = ExpColors.SandLight,
            title = { Text("Budget Cycle", fontFamily = ExpSerif) },
            text = {
                Column {
                    listOf("monthly", "yearly").forEach { c ->
                        Row(Modifier.fillMaxWidth().clickable { onSetBudgetCycle(c); showCycleDialog = false }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = state.budgetCycle == c, onClick = { onSetBudgetCycle(c); showCycleDialog = false })
                            Spacer(Modifier.width(8.dp))
                            Text(c.replaceFirstChar { it.uppercase() }, color = ExpColors.Ink)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showCycleDialog = false }) { Text("Cancel") } },
        )
    }
    if (showDailyLimitDialog) {
        var text by remember { mutableStateOf(if (state.budget.dailyLimit > 0) state.budget.dailyLimit.toString() else "") }
        AlertDialog(
            onDismissRequest = { showDailyLimitDialog = false },
            containerColor = ExpColors.SandLight,
            title = { Text("Daily Limit", fontFamily = ExpSerif) },
            text = {
                OutlinedTextField(value = text, onValueChange = { v -> if (v.matches(Regex("""\\d{0,7}(\\.\\d{0,2})?"""))) text = v }, singleLine = true, colors = fieldColors())
            },
            confirmButton = {
                TextButton(onClick = {
                    val v = text.toDoubleOrNull() ?: 0.0
                    onSetDailyLimit(v)
                    showDailyLimitDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDailyLimitDialog = false }) { Text("Cancel") } },
        )
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
