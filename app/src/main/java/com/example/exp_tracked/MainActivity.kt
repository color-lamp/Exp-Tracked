package com.example.exp_tracked

import android.os.Build
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
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
                        onPayAndLog = { draft ->
                            if (draft.payAndLog) {
                                vm.logPendingExpense(draft)
                                vm.launchUpiAppHome(UpiApp.OTHER, draft.vpa.ifBlank { null })
                            } else {
                                vm.saveExpense(draft)
                            }
                        },
                        onSetPreferredUpiApp = vm::setPreferredUpiApp,
                        onConfirmPending = vm::confirmPendingExpenses,
                        onDiscardPending = vm::discardPendingExpenses,
                        onOpenUpiApp = { vm.launchUpiAppHome(UpiApp.OTHER, null) },
                        onSetBudgetCycle = vm::setBudgetCycle,
                        onRenewBudget = vm::renewBudget,
                        onSetDailyLimit = vm::setDailyLimit,
                        onHasPin = vm::hasPin,
                        onSetPin = vm::setPin,
                        onVerifyPin = vm::verifyPin,
                    ),
                )
            }
        }
    }
}