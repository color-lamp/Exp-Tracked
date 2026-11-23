package com.exptracked.ui.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class ModelTest {

    @Test
    fun parsePayee_decodesPercent20() {
        val raw = "upi://pay?pa=shop@upi&pn=Ramesh%20Store&am=100"
        assertEquals("Ramesh Store", parsePayee(raw))
    }

    @Test
    fun parsePayee_decodesPlus() {
        val raw = "upi://pay?pa=shop@upi&pn=Ramesh+Store&am=100"
        assertEquals("Ramesh Store", parsePayee(raw))
    }

    @Test
    fun parsePayee_fallsBackToPa() {
        val raw = "upi://pay?pa=shop@upi&am=100"
        assertEquals("shop@upi", parsePayee(raw))
    }

    @Test
    fun parsePayee_fallsBackToRawForNonUpi() {
        val raw = "https://example.com"
        assertEquals(raw, parsePayee(raw))
    }

    @Test
    fun parsePayeeVpa_extractsPa() {
        val raw = "upi://pay?pa=vpa@upi&pn=Name"
        assertEquals("vpa@upi", parsePayeeVpa(raw))
    }

    @Test
    fun parsePayeeVpa_emptyForNonUpi() {
        assertEquals("", parsePayeeVpa("hello"))
    }

    @Test
    fun parseAmount_extractsAm() {
        assertEquals("450.0", parseAmount("upi://pay?pa=a@b&am=450.0"))
    }

    @Test
    fun parseTr_extractsTr() {
        assertEquals("ref123", parseTr("upi://pay?pa=a@b&tr=ref123"))
    }

    @Test
    fun parseMc_extractsMc() {
        assertEquals("5411", parseMc("upi://pay?pa=a@b&mc=5411"))
    }

    @Test
    fun budgetEvaluate_blockedWhenOverLimit() {
        val budget = BudgetState(spent = 14000.0, limit = 15000.0)
        val gate = budget.evaluate(2000.0)
        assertTrue(gate is ExpenseGate.Blocked)
    }

    @Test
    fun budgetEvaluate_allowedWhenUnder() {
        val budget = BudgetState(spent = 1000.0, limit = 15000.0)
        val gate = budget.evaluate(500.0)
        assertTrue(gate is ExpenseGate.Allowed)
    }

    @Test
    fun budgetEvaluate_warningNearLimit() {
        val budget = BudgetState(spent = 10000.0, limit = 15000.0, warningThreshold = 0.8f)
        val gate = budget.evaluate(3000.0)
        assertTrue(gate is ExpenseGate.AllowedWithWarning)
    }
}