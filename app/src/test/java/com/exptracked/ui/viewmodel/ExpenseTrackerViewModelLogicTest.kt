package com.exptracked.ui.viewmodel

import com.exptracked.ui.model.BudgetState
import com.exptracked.ui.model.DashboardUiState
import com.exptracked.ui.model.ExpenseCategory
import com.exptracked.ui.model.ExpenseItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ExpenseTrackerViewModelLogicTest {

    private fun sampleState(): DashboardUiState {
        val now = LocalDateTime.now()
        val expenses = listOf(
            ExpenseItem(1, "Shop", 100.0, ExpenseCategory.FOOD, now, pendingConfirmation = true),
            ExpenseItem(2, "Cafe", 50.0, ExpenseCategory.FOOD, now),
        )
        return DashboardUiState(
            userName = "You",
            greeting = "Hi",
            budget = BudgetState(spent = 150.0, limit = 1000.0),
            expenses = expenses,
            weeklySpend = emptyList(),
            offers = emptyList(),
        )
    }

    @Test
    fun applyConfirmPending_marksAllConfirmed() {
        val state = sampleState()
        val result = ExpenseTrackerViewModel.applyConfirmPending(state)
        assertTrue(result.expenses.none { it.pendingConfirmation })
    }

    @Test
    fun applyDiscardPending_removesPendingAndAdjustsBudget() {
        val state = sampleState()
        val result = ExpenseTrackerViewModel.applyDiscardPending(state)
        assertEquals(1, result.expenses.size)
        assertEquals(50.0, result.budget.spent, 0.001)
        assertEquals(50.0, result.expenses[0].amount, 0.001)
    }

    @Test
    fun applyLogPending_prependsItem() {
        val state = sampleState()
        val item = ExpenseItem(99, "New", 25.0, ExpenseCategory.FOOD, LocalDateTime.now(), pendingConfirmation = true)
        val result = ExpenseTrackerViewModel.applyLogPending(state, item)
        assertEquals(3, result.expenses.size)
        assertEquals("New", result.expenses[0].payee)
        assertEquals(175.0, result.budget.spent, 0.001)
    }
}