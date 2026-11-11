package com.example.exp_tracked

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.exptracked.ui.ExptrackedApp
import com.exptracked.ui.ExptrackedActions
import com.exptracked.ui.ExptrackedTheme
import com.exptracked.ui.ExpenseGate
import com.exptracked.ui.ExpenseItem
import com.exptracked.ui.SampleData
import com.exptracked.ui.DashboardUiState
import com.exptracked.ui.evaluate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                                    com.exptracked.ui.CoachMessage(q, true) +
                                    com.exptracked.ui.CoachMessage("Placeholder reply. Wire this to your coach backend.", false),
                            )
                        },
                    ),
                )
            }
        }
    }
}