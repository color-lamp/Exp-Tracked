package com.exptracked.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseTrackerViewModelTest {

    @Test
    fun buildUpiUri_containsCorrectParams() {
        val uri = ExpenseTrackerViewModel.buildUpiUri(
            payee = "merchant@upi",
            payeeName = "Merchant Name",
            amount = 450.0,
        )

        assertEquals("upi://pay?pa=merchant%40upi&pn=Merchant%20Name&am=450.0&cu=INR", uri)
    }

    @Test
    fun buildUpiUri_handlesZeroAmount() {
        val uri = ExpenseTrackerViewModel.buildUpiUri(
            payee = "a@b",
            payeeName = "A",
            amount = 0.0,
        )
        assertEquals("upi://pay?pa=a%40b&pn=A&am=0.0&cu=INR", uri)
    }
}