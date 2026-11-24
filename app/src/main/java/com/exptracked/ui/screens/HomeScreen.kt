@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.screens

import com.exptracked.ui.model.*
import com.exptracked.ui.theme.*
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
 *  HOME
 * ========================================================================================== */

@Composable
fun HomeScreen(
    state: DashboardUiState,
    onSeeAllHistory: () -> Unit,
    onOverrideChange: (Boolean) -> Unit,
    onAvatarClick: () -> Unit,
    onOpenUpiApp: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { HomeHeader(state.greeting, state.userName, onAvatarClick) }
        item { BudgetCard(state.budget, onOverrideChange, onOpenUpiApp) }
        if (state.budget.dailyLimit > 0) {
            item {
                val color = when (dailyBudgetStatus(state.dailySpent, state.budget.dailyLimit)) {
                    DailyBudgetStatus.GREEN -> Color(0xFF4CAF50)
                    DailyBudgetStatus.YELLOW -> ExpColors.Warning
                    DailyBudgetStatus.RED -> ExpColors.Danger
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(color))
                    Spacer(Modifier.width(8.dp))
                    Text("Daily: ${state.dailySpent.asInr()} / ${state.budget.dailyLimit.asInr()}", color = ExpColors.Ink, fontSize = 13.sp)
                }
            }
        }
        item {
            var showCalendar by remember { mutableStateOf(false) }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { showCalendar = true }) { Icon(Icons.Outlined.DateRange, "Calendar", tint = ExpColors.Ink) }
            }
            if (showCalendar) {
                val grouped = state.expenses.groupBy { it.dateTime.toLocalDate() }.mapValues { it.value.sumOf { e -> e.amount } }
                AlertDialog(
                    onDismissRequest = { showCalendar = false },
                    containerColor = ExpColors.SandLight,
                    title = { Text("Daily Spend", fontFamily = ExpSerif) },
                    text = {
                        LazyColumn(Modifier.height(300.dp)) {
                            items(grouped.toList().sortedByDescending { it.first }) { (d, amt) ->
                                Text("$d — ${amt.asInr()}", color = ExpColors.Ink)
                            }
                        }
                    },
                    confirmButton = { TextButton(onClick = { showCalendar = false }) { Text("Close") } },
                )
            }
        }
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
    onOpenUpiApp: () -> Unit = {},
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
                Icon(Icons.Outlined.AccountBalanceWallet, "Open UPI app", tint = accent, modifier = Modifier.clickable(onClick = onOpenUpiApp))
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
                Text("Limit ${budget.limit.asInr()}", color = ExpColors.OnOlive.copy(alpha = 0.8f), fontSize = 13.sp)
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




