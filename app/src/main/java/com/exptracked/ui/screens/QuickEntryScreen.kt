@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.screens

import com.exptracked.ui.model.*
import com.exptracked.ui.theme.*
import com.exptracked.ui.components.*
import com.exptracked.ui.components.CameraPreview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickEntryScreen(
    initialPayee: String,
    initialVpa: String = "",
    onBack: () -> Unit,
    onPayAndLog: (ExpenseDraft) -> Unit,
    onScan: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var amountText by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(ExpenseCategory.FOOD) }
    var payee by rememberSaveable { mutableStateOf(initialPayee) }
    var note by rememberSaveable { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    val canPay = amount != null && amount > 0 && payee.isNotBlank()

    RouteScaffold(modifier) {
        ScreenTopBar("Quick Entry", onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            OutlinedTextField(
                value = amountText,
                onValueChange = { v -> if (v.matches(Regex("""\d{0,7}(\.\d{0,2})?"""))) amountText = v },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 32.sp, fontFamily = ExpSerif, color = ExpColors.Ink),
                prefix = { Text("₹ ", fontSize = 32.sp, fontFamily = ExpSerif, color = ExpColors.Ink) },
                placeholder = { Text("0", fontSize = 32.sp, fontFamily = ExpSerif, color = ExpColors.InkSoft) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors(),
            )

            OutlinedTextField(
                value = payee,
                onValueChange = { payee = it },
                label = { Text("Person / Shop") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )

            Text("Select Category", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 16.sp)
            ExpenseCategory.entries.chunked(3).forEach { rowCats ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowCats.forEach { c ->
                        CategoryTile(c, selected = c == category, onClick = { category = c }, modifier = Modifier.weight(1f))
                    }
                    repeat(3 - rowCats.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Add Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Scan a QR code",
                color = ExpColors.Accent,
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onScan).padding(vertical = 4.dp),
            )
        }
        PrimaryButton(
            "Pay & Log",
            onClick = { onPayAndLog(ExpenseDraft(amount ?: 0.0, category, payee.trim(), note.trim(), vpa = initialVpa)) },
            enabled = canPay,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}