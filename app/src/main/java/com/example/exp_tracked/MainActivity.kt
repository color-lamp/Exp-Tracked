package com.example.exp_tracked

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.exptracked.ui.CoachMessage
import com.exptracked.ui.DashboardUiState
import com.exptracked.ui.EditNameDialog
import com.exptracked.ui.ExptrackedApp
import com.exptracked.ui.ExptrackedActions
import com.exptracked.ui.ExptrackedTheme
import com.exptracked.ui.ExpenseCategory
import com.exptracked.ui.ExpenseGate
import com.exptracked.ui.ExpenseItem
import com.exptracked.ui.ThemeMode
import com.exptracked.ui.evaluate
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        setContent {
            var state by remember { mutableStateOf(loadState(prefs)) }

            // Persist whenever state changes
            LaunchedEffect(state) { saveState(prefs, state) }

            ExptrackedTheme(state.themeMode) {
                ExptrackedApp(
                    state = state,
                    actions = ExptrackedActions(
                        onSetThemeMode = { state = state.copy(themeMode = it) },
                        onSetUserName = { name -> state = state.copy(userName = name) },
                        onSaveExpense = { d ->
                            if (state.budget.evaluate(d.amount) !is ExpenseGate.Blocked) {
                                val item = ExpenseItem(
                                    System.nanoTime(),
                                    d.payee,
                                    d.amount,
                                    d.category,
                                    d.dateTime,
                                )
                                val newExpenses = listOf(item) + state.expenses
                                state = state.copy(
                                    expenses = newExpenses,
                                    budget = state.budget.copy(spent = state.budget.spent + d.amount),
                                    weeklySpend = computeWeeklySpend(newExpenses),
                                    healthScore = computeHealth(state.budget.copy(spent = state.budget.spent + d.amount)),
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
    }

    companion object {
        private const val PREFS = "exp_tracked_prefs"
    }
}

/* ----------------------------- Persistence helpers ----------------------------- */

private fun loadState(prefs: android.content.SharedPreferences): DashboardUiState {
    val name = prefs.getString("userName", "") ?: ""
    val email = prefs.getString("email", "you@example.com") ?: "you@example.com"
    val limit = prefs.getFloat("budgetLimit", 15000f).toDouble()
    val spent = prefs.getFloat("budgetSpent", 0f).toDouble()
    val override = prefs.getBoolean("overrideEnabled", false)
    val themeName = prefs.getString("themeMode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
    val themeMode = runCatching { ThemeMode.valueOf(themeName) }.getOrDefault(ThemeMode.DARK)
    val expenses = loadExpenses(prefs.getString("expenses", "[]") ?: "[]")
    val weekly = computeWeeklySpend(expenses)
    val budget = com.exptracked.ui.BudgetState(spent = spent, limit = limit, overrideEnabled = override)
    return DashboardUiState(
        userName = if (name.isBlank()) "You" else name,
        email = email,
        greeting = greetingFor(java.time.LocalDateTime.now().hour),
        budget = budget,
        expenses = expenses,
        weeklySpend = weekly,
        offers = emptyList(),
        healthScore = computeHealth(budget),
        savingsGoalCount = 0,
        coachMessages = emptyList(),
        pendingPayee = "",
        themeMode = themeMode,
    )
}

private fun saveState(prefs: android.content.SharedPreferences, state: DashboardUiState) {
    prefs.edit().apply {
        putString("userName", state.userName)
        putString("email", state.email)
        putFloat("budgetLimit", state.budget.limit.toFloat())
        putFloat("budgetSpent", state.budget.spent.toFloat())
        putBoolean("overrideEnabled", state.budget.overrideEnabled)
        putString("themeMode", state.themeMode.name)
        putString("expenses", saveExpenses(state.expenses))
    }.apply()
}

private fun loadExpenses(json: String): List<ExpenseItem> {
    val list = mutableListOf<ExpenseItem>()
    runCatching {
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                ExpenseItem(
                    id = o.getLong("id"),
                    payee = o.getString("payee"),
                    amount = o.getDouble("amount"),
                    category = ExpenseCategory.valueOf(o.getString("category")),
                    dateTime = LocalDateTime.parse(o.getString("dateTime")),
                )
            )
        }
    }
    return list
}

private fun saveExpenses(expenses: List<ExpenseItem>): String {
    val arr = JSONArray()
    expenses.forEach { e ->
        arr.put(
            JSONObject().apply {
                put("id", e.id)
                put("payee", e.payee)
                put("amount", e.amount)
                put("category", e.category.name)
                put("dateTime", e.dateTime.toString())
            }
        )
    }
    return arr.toString()
}

private fun computeWeeklySpend(expenses: List<ExpenseItem>): List<com.exptracked.ui.DailySpend> {
    val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val totals = DoubleArray(7)
    expenses.forEach { e ->
        val idx = e.dateTime.dayOfWeek.value % 7 // Monday=1..Sunday=7 -> Sun=0
        totals[idx] += e.amount
    }
    return dayLabels.mapIndexed { i, label -> com.exptracked.ui.DailySpend(label, totals[i]) }
}

private fun computeHealth(budget: com.exptracked.ui.BudgetState): Int {
    if (budget.limit <= 0.0) return 0
    val fraction = (budget.spent / budget.limit).coerceIn(0.0, 2.0)
    return if (fraction <= 1.0) {
        (100 - fraction * 30).toInt().coerceIn(70, 100)
    } else {
        (70 - (fraction - 1.0) * 40).toInt().coerceIn(0, 70)
    }
}

private fun greetingFor(hour: Int): String = com.exptracked.ui.greetingFor(hour)