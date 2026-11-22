package com.exptracked.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exptracked.ui.model.BudgetState
import com.exptracked.ui.model.CoachMessage
import com.exptracked.ui.model.DashboardUiState
import com.exptracked.ui.model.DailySpend
import com.exptracked.ui.model.ExpenseCategory
import com.exptracked.ui.model.ExpenseDraft
import com.exptracked.ui.model.ExpenseGate
import com.exptracked.ui.model.ExpenseItem
import com.exptracked.ui.theme.ThemeMode
import com.exptracked.ui.model.UpiApp
import com.exptracked.ui.model.asInr
import com.exptracked.ui.model.evaluate
import com.exptracked.ui.model.greetingFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime

class ExpenseTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(loadState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private fun loadState(): DashboardUiState {
        val name = prefs.getString("userName", "") ?: ""
        val email = prefs.getString("email", "you@example.com") ?: "you@example.com"
        val limit = prefs.getFloat("budgetLimit", 15000f).toDouble()
        val spent = prefs.getFloat("budgetSpent", 0f).toDouble()
        val override = prefs.getBoolean("overrideEnabled", false)
        val themeName = prefs.getString("themeMode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        val themeMode = runCatching { ThemeMode.valueOf(themeName) }.getOrDefault(ThemeMode.DARK)
        val preferredUpiPackage = prefs.getString("preferredUpiPackage", null)
        val expenses = loadExpenses(prefs.getString("expenses", "[]") ?: "[]")
        val weekly = computeWeeklySpend(expenses)
        val budget = BudgetState(spent = spent, limit = limit, overrideEnabled = override)
        return DashboardUiState(
            userName = if (name.isBlank()) "You" else name,
            email = email,
            greeting = greetingFor(LocalDateTime.now().hour),
            budget = budget,
            expenses = expenses,
            weeklySpend = weekly,
            offers = emptyList(),
            healthScore = computeHealth(budget),
            savingsGoalCount = 0,
            coachMessages = emptyList(),
            pendingPayee = "",
            preferredUpiPackage = preferredUpiPackage,
            themeMode = themeMode,
        )
    }

    private fun persist(s: DashboardUiState) {
        prefs.edit().apply {
            putString("userName", s.userName)
            putString("email", s.email)
            putFloat("budgetLimit", s.budget.limit.toFloat())
            putFloat("budgetSpent", s.budget.spent.toFloat())
            putBoolean("overrideEnabled", s.budget.overrideEnabled)
            putString("themeMode", s.themeMode.name)
            putString("preferredUpiPackage", s.preferredUpiPackage)
            putString("expenses", saveExpenses(s.expenses))
        }.apply()
    }

    fun setThemeMode(mode: ThemeMode) {
        _state.update { it.copy(themeMode = mode) }
        persist(_state.value)
    }

    fun setUserName(name: String) {
        _state.update { it.copy(userName = name) }
        persist(_state.value)
    }

    fun setPreferredUpiApp(packageName: String?) {
        _state.update { it.copy(preferredUpiPackage = packageName) }
        prefs.edit().putString("preferredUpiPackage", packageName).apply()
    }

    fun setOverride(enabled: Boolean) {
        _state.update { it.copy(budget = it.budget.copy(overrideEnabled = enabled)) }
        persist(_state.value)
    }

    fun setLimit(limit: Double) {
        _state.update { it.copy(budget = it.budget.copy(limit = limit)) }
        persist(_state.value)
    }

    fun pickContact() {
        _state.update { it.copy(pendingPayee = "Rohan Kulkarni") }
    }

    fun consumePayee() {
        _state.update { it.copy(pendingPayee = "") }
    }

    fun askCoach(q: String) {
        _state.update {
            it.copy(
                coachMessages = it.coachMessages +
                    CoachMessage(q, true) +
                    CoachMessage("Placeholder reply. Wire this to your coach backend.", false),
            )
        }
    }

    /**
     * Redirects to a UPI app to pay [amount] to [payee] ([payeeName]).
     * Builds a upi://pay?pa=...&pn=...&am=...&cu=INR URI and fires ACTION_VIEW.
     * No state mutation, no persistence, no result parsing.
     */
    fun launchUpiPayment(payee: String, payeeName: String, amount: Double, upiApp: UpiApp, tr: String = "") {
        val context = getApplication<Application>()
        val uri = Uri.parse(buildUpiUri(payee, payeeName, amount, tr))

        // Query PackageManager for apps that can handle upi://pay URIs.
        val upiIntent = Intent(Intent.ACTION_VIEW, uri)
        val resolveInfos = context.packageManager.queryIntentActivities(upiIntent, 0)

        if (resolveInfos.isNullOrEmpty()) {
            android.widget.Toast.makeText(context, "No UPI app available", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        val preferred = _state.value.preferredUpiPackage
        val pkg = preferred?.takeIf { isPackageInstalled(context, it) } ?: upiApp.packageName
        // Target the selected app specifically when a package is known.
        if (pkg != null) {
            try {
                val targeted = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage(pkg)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(targeted)
                return
            } catch (e: android.content.ActivityNotFoundException) {
                // Selected app not installed — fall through to generic chooser.
            }
        }

        // Fallback: generic upi:// intent (Android shows chooser / opens directly).
        try {
            val generic = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(generic)
        } catch (e: android.content.ActivityNotFoundException) {
            android.widget.Toast.makeText(context, "No UPI app available", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPackageInstalled(context: android.content.Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            false
        }
    }

    companion object {
        private const val PREFS = "exp_tracked_prefs"

        fun buildUpiUri(payee: String, payeeName: String, amount: Double, tr: String = ""): String {
            val base = "upi://pay?pa=${uriEncode(payee)}&pn=${uriEncode(payeeName)}&am=$amount&cu=INR"
            return if (tr.isNotBlank()) "$base&tr=${uriEncode(tr)}" else base
        }

        private fun uriEncode(s: String): String {
            val sb = StringBuilder()
            for (ch in s) {
                if (isUnreserved(ch)) {
                    sb.append(ch)
                } else {
                    val bytes = ch.toString().toByteArray(Charsets.UTF_8)
                    for (b in bytes) {
                        sb.append('%')
                        sb.append(HEX_DIGITS[(b.toInt() and 0xf0) shr 4])
                        sb.append(HEX_DIGITS[b.toInt() and 0x0f])
                    }
                }
            }
            return sb.toString()
        }

        private fun isUnreserved(c: Char): Boolean =
            (c in 'A'..'Z') || (c in 'a'..'z') || (c in '0'..'9') ||
                c == '-' || c == '_' || c == '.' || c == '~' ||
                c == '!' || c == '\'' || c == '(' || c == ')' || c == '*'

        private val HEX_DIGITS = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F',
        )
    }

    fun saveExpense(draft: ExpenseDraft) {
        val current = _state.value
        if (current.budget.evaluate(draft.amount) is ExpenseGate.Blocked) return
        val item = ExpenseItem(
            System.nanoTime(),
            draft.payee,
            draft.amount,
            draft.category,
            draft.dateTime,
        )
        val newExpenses = listOf(item) + current.expenses
        val newBudget = current.budget.copy(spent = current.budget.spent + draft.amount)
        _state.update {
            it.copy(
                expenses = newExpenses,
                budget = newBudget,
                weeklySpend = computeWeeklySpend(newExpenses),
                healthScore = computeHealth(newBudget),
            )
        }
        persist(_state.value)
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

    private fun computeWeeklySpend(expenses: List<ExpenseItem>): List<DailySpend> {
        val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val totals = DoubleArray(7)
        expenses.forEach { e ->
            val idx = e.dateTime.dayOfWeek.value % 7
            totals[idx] += e.amount
        }
        return dayLabels.mapIndexed { i, label -> DailySpend(label, totals[i]) }
    }

    private fun computeHealth(budget: BudgetState): Int {
        if (budget.limit <= 0.0) return 0
        val fraction = (budget.spent / budget.limit).coerceIn(0.0, 2.0)
        return if (fraction <= 1.0) {
            (100 - fraction * 30).toInt().coerceIn(70, 100)
        } else {
            (70 - (fraction - 1.0) * 40).toInt().coerceIn(0, 70)
        }
    }
}