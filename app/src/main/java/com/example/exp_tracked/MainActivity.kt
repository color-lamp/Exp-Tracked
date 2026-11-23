package com.example.exp_tracked

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exptracked.ui.ExptrackedApp
import com.exptracked.ui.model.ExptrackedActions
import com.exptracked.ui.model.ExpenseDraft
import com.exptracked.ui.model.UpiApp
import com.exptracked.ui.screens.EditNameDialog
import com.exptracked.ui.theme.ExptrackedTheme
import com.exptracked.ui.viewmodel.ExpenseTrackerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: ExpenseTrackerViewModel = viewModel()
            val state by vm.state.collectAsState()
            ExptrackedTheme(state.themeMode) {
                ExptrackedApp(
                    state = state,
                    actions = ExptrackedActions(
                        onSetThemeMode = vm::setThemeMode,
                        onSetUserName = vm::setUserName,
                        onSaveExpense = vm::saveExpense,
                        onSetOverride = vm::setOverride,
                        onSetLimit = vm::setLimit,
                        onPickContact = vm::pickContact,
                        onPayeeConsumed = vm::consumePayee,
                        onAskCoach = vm::askCoach,
                        onPayAndLog = { draft -> vm.logPendingExpense(draft); vm.launchUpiAppHome(UpiApp.OTHER, draft.vpa.ifBlank { null }) },
                        onSetPreferredUpiApp = vm::setPreferredUpiApp,
                        onSetPendingExpense = vm::setPendingExpense,
                        onConfirmPendingExpense = vm::confirmPendingExpense,
                        onDiscardPendingExpense = vm::discardPendingExpense,
                        onAppResumed = vm::onAppResumed,
                        onConfirmPending = vm::confirmPendingExpenses,
                        onDiscardPending = vm::discardPendingExpenses,
                    ),
                )
            }
        }
    }
}