package com.exptracked.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.view.WindowCompat
import kotlin.math.roundToInt
import com.exptracked.ui.model.*
import com.exptracked.ui.theme.*

@Composable
internal fun StatusBanner(icon: ImageVector, text: String, color: Color) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(color.copy(alpha = 0.16f)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = color, fontSize = 13.sp)
    }
}

@Composable
fun SectionTitle(title: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = ExpColors.Ink, fontSize = 18.sp, fontFamily = ExpSerif, modifier = Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Text(actionLabel, color = ExpColors.InkSoft, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onAction))
        }
    }
}

@Composable
fun TransactionRow(item: ExpenseItem, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(48.dp).clip(CircleShape).background(ExpColors.Sand),
            contentAlignment = Alignment.Center,
        ) { Icon(item.category.icon, null, tint = item.category.color) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.payee, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${item.category.label} • ${item.dateTime.format(timeFmt)}", color = ExpColors.InkSoft, fontSize = 12.sp)
        }
        Text("-" + item.amount.asInr(), color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SimulatedScanner(onResult: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(ExpColors.OliveDark)
            .clickable { onResult("upi://pay?pa=sagarmess@okhdfc&pn=Sagar%20Mess") },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.QrCodeScanner, null, tint = ExpColors.OnOlive.copy(alpha = 0.3f), modifier = Modifier.size(96.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                "Camera preview goes here\n(tap to simulate a scan)",
                color = ExpColors.OnOlive.copy(alpha = 0.6f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun EditLimitDialog(currentLimit: Double, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var text by remember { mutableStateOf(if (currentLimit > 0) currentLimit.roundToInt().toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExpColors.SandLight,
        title = { Text("Monthly limit", fontFamily = ExpSerif) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { v -> if (v.all(Char::isDigit) && v.length <= 8) text = v },
                prefix = { Text("₹ ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
            )
        },
        confirmButton = {
            TextButton(onClick = { text.toDoubleOrNull()?.let(onConfirm) }, enabled = text.toDoubleOrNull() != null) { Text("Set") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ExpColors.Accent,
    unfocusedBorderColor = ExpColors.Outline,
    focusedLabelColor = ExpColors.Accent,
    cursorColor = ExpColors.Accent,
    focusedContainerColor = ExpColors.SandLight,
    unfocusedContainerColor = ExpColors.SandLight,
)


@Composable
fun EditNameDialog(currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExpColors.SandLight,
        title = { Text("Your name", fontFamily = ExpSerif) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { v -> if (v.length <= 40) text = v },
                singleLine = true,
                colors = fieldColors(),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onConfirm(text.trim()) }, enabled = text.isNotBlank()) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
