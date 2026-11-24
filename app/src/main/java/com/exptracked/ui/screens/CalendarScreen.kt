@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.screens

import com.exptracked.ui.model.*
import com.exptracked.ui.theme.*
import com.exptracked.ui.components.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(expenses: List<ExpenseItem>, dailyLimit: Double, monthlyLimit: Double, onBack: () -> Unit) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    val dailyTotals = remember(expenses) { expenses.groupBy { it.dateTime.toLocalDate() }.mapValues { it.value.sumOf { e -> e.amount } } }
    val threshold = if (dailyLimit > 0) dailyLimit else (monthlyLimit / 30.0)

    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Calendar", onBack)
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { month = month.minusMonths(1) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Prev") }
            Text(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")), fontFamily = ExpSerif, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = { month = month.plusMonths(1) }) { Icon(Icons.AutoMirrored.Outlined.ArrowForward, "Next") }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { d ->
                Text(d, Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, color = ExpColors.InkSoft)
            }
        }
        val firstDay = month.atDay(1)
        val startOffset = firstDay.dayOfWeek.value % 7
        val daysInMonth = month.lengthOfMonth()
        val cells = (0 until (startOffset + daysInMonth)).map { idx ->
            if (idx < startOffset) null else LocalDate.of(month.year, month.month, idx - startOffset + 1)
        }
        LazyVerticalGrid(columns = GridCells.Fixed(7), Modifier.fillMaxSize().padding(8.dp)) {
            items(cells) { day ->
                if (day == null) {
                    Spacer(Modifier.aspectRatio(1f))
                } else {
                    val total = dailyTotals[day] ?: 0.0
                    val color = when {
                        total <= 0.0 -> Color.Transparent
                        threshold <= 0 -> ExpColors.InkSoft
                        total > threshold -> ExpColors.Danger
                        total >= 0.5 * threshold -> ExpColors.Warning
                        else -> Color(0xFF4CAF50)
                    }
                    Box(
                        Modifier.aspectRatio(1f).padding(2.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = if (total > 0) 0.3f else 0f)).clickable { if (total > 0) selectedDay = day },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(day.dayOfMonth.toString(), color = ExpColors.Ink, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    if (selectedDay != null) {
        val dayExpenses = expenses.filter { it.dateTime.toLocalDate() == selectedDay }
        AlertDialog(
            onDismissRequest = { selectedDay = null },
            containerColor = ExpColors.SandLight,
            title = { Text(selectedDay.toString(), fontFamily = ExpSerif) },
            text = {
                Column {
                    dayExpenses.forEach { e ->
                        Text("${e.payee} — ${e.amount.asInr()} (${e.category.label})", color = ExpColors.Ink, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { selectedDay = null }) { Text("Close") } },
        )
    }
}