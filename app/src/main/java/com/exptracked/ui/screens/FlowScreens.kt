@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.screens

import com.exptracked.ui.model.*
import com.exptracked.ui.theme.*
import com.exptracked.ui.components.*
import com.exptracked.ui.components.CameraPreview

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

/* ============================================================================================
 *  FLOW SCREENS: Scan QR -> Add Expense -> Review & Pay -> Success
 * ========================================================================================== */

@Composable
fun RouteScaffold(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxSize().background(ExpColors.Cream).navigationBarsPadding(),
        content = content,
    )
}

@Composable
fun ScreenTopBar(title: String, onBack: () -> Unit, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().background(ExpColors.Olive).statusBarsPadding().padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = ExpColors.OnOlive) }
        Text(title, fontFamily = ExpSerif, fontSize = 20.sp, color = ExpColors.OnOlive, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ExpColors.Butter,
            contentColor = ExpColors.Green,
            disabledContainerColor = ExpColors.Outline,
            disabledContentColor = ExpColors.InkSoft,
        ),
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

/* ---- Scan QR ---- */

@Composable
fun ScanQrScreen(
    onBack: () -> Unit,
    onScanned: (String) -> Unit,
    onEnterManually: () -> Unit,
    onPickContact: () -> Unit,
    modifier: Modifier = Modifier,
    scanner: @Composable (onResult: (String) -> Unit) -> Unit = { CameraPreview(it) },
) {
    RouteScaffold(modifier) {
        ScreenTopBar("Scan QR", onBack)
        Box(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(24.dp)),
        ) {
            scanner(onScanned)
            ScanFrameOverlay(Modifier.align(Alignment.Center).size(230.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Scan any UPI QR to pay",
            color = ExpColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Or enter UPI ID",
            color = ExpColors.InkSoft,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().clickable(onClick = onEnterManually).padding(8.dp),
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onEnterManually,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ExpColors.Butter, contentColor = ExpColors.Green),
            ) { Text("Enter Amount\nManually", fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 15.sp) }
            OutlinedButton(
                onClick = onPickContact,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ExpColors.Accent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpColors.Accent),
            ) {
                Icon(Icons.Outlined.Contacts, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Pay a Contact", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ScanFrameOverlay(modifier: Modifier = Modifier) {
    val c = ExpColors.OnOlive
    Canvas(modifier) {
        val len = 36.dp.toPx()
        val sw = 5.dp.toPx()
        fun corner(x: Float, y: Float, dx: Float, dy: Float) {
            drawLine(c, Offset(x, y), Offset(x + dx * len, y), sw, StrokeCap.Round)
            drawLine(c, Offset(x, y), Offset(x, y + dy * len), sw, StrokeCap.Round)
        }
        corner(0f, 0f, 1f, 1f)
        corner(size.width, 0f, -1f, 1f)
        corner(0f, size.height, 1f, -1f)
        corner(size.width, size.height, -1f, -1f)
    }
}

/** Stand-in for the camera. Replace with CameraX PreviewView + ML Kit barcode scanning via AndroidView. */


/* ---- Add Expense ---- */

@Composable
fun AddExpenseScreen(
    initialPayee: String,
    initialVpa: String = "",
    initialAmount: String = "",
    initialTr: String = "",
    initialMc: String = "",
    initialRawUri: String = "",
    onBack: () -> Unit,
    onNext: (ExpenseDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amountText by rememberSaveable { mutableStateOf(initialAmount) }
    var category by rememberSaveable { mutableStateOf(ExpenseCategory.FOOD) }
    var payee by rememberSaveable { mutableStateOf(initialPayee) }
    var vpa by rememberSaveable { mutableStateOf(initialVpa) }
    var tr by rememberSaveable { mutableStateOf(initialTr) }
    var mc by rememberSaveable { mutableStateOf(initialMc) }
    var rawUri by rememberSaveable { mutableStateOf(initialRawUri) }
    var note by rememberSaveable { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    val canNext = amount != null && amount > 0 && payee.isNotBlank()

    RouteScaffold(modifier) {
        ScreenTopBar("Add Expense", onBack)
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
                trailingIcon = if (amountText.isNotEmpty()) {
                    { IconButton(onClick = { amountText = "" }) { Icon(Icons.Outlined.Close, "Clear", tint = ExpColors.InkSoft) } }
                } else null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors(),
            )

            Text("Select Category", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 16.sp)
            ExpenseCategory.entries.chunked(4).forEach { rowCats ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowCats.forEach { c ->
                        CategoryTile(c, selected = c == category, onClick = { category = c }, modifier = Modifier.weight(1f))
                    }
                    repeat(4 - rowCats.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            OutlinedTextField(
                value = payee,
                onValueChange = { payee = it },
                label = { Text("Contact / Merchant") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            OutlinedTextField(
                value = vpa,
                onValueChange = { vpa = it },
                label = { Text("UPI ID (e.g. name@upi)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Add Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors(),
            )
            Spacer(Modifier.height(4.dp))
        }
        PrimaryButton(
            "Next",
            onClick = { onNext(ExpenseDraft(amount ?: 0.0, category, payee.trim(), note.trim(), vpa.trim(), tr.trim(), mc.trim(), rawUri)) },
            enabled = canNext,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )
    }
}

@Composable
fun CategoryTile(category: ExpenseCategory, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ExpColors.SandLight else ExpColors.Sand)
            .border(if (selected) 2.dp else 1.dp, if (selected) ExpColors.Accent else ExpColors.Outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(category.icon, null, tint = if (selected) ExpColors.Accent else ExpColors.InkSoft)
        Text(
            category.label,
            fontSize = 10.sp,
            color = ExpColors.Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/* ---- Review & Pay ---- */

@Composable
fun ReviewPayScreen(
    draft: ExpenseDraft,
    budget: BudgetState,
    offers: List<DailyOffer>,
    onBack: () -> Unit,
    onOverrideChange: (Boolean) -> Unit,
    onProceed: (payable: Double, app: UpiApp) -> Unit,
    onLogOnly: (payable: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bestOffer = remember(draft.amount, offers) {
        offers.filter { it.type != OfferType.COUPON && it.savingsOn(draft.amount) > 0 }
            .maxByOrNull { it.savingsOn(draft.amount) }
    }
    val savings = bestOffer?.savingsOn(draft.amount) ?: 0.0
    val payable = draft.amount - savings
    val gate = budget.evaluate(payable)
    val blocked = gate is ExpenseGate.Blocked
    var app by remember { mutableStateOf(UpiApp.GPAY) }

    RouteScaffold(modifier) {
        ScreenTopBar("Review & Pay", onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Merchant
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(CircleShape).background(ExpColors.Cream), contentAlignment = Alignment.Center) {
                        Icon(draft.category.icon, null, tint = draft.category.color)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(draft.payee, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (draft.note.isBlank()) draft.category.label else "${draft.category.label} • ${draft.note}",
                            color = ExpColors.InkSoft,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            // Amount
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Column(Modifier.padding(16.dp)) {
                    Text(draft.amount.asInr(), color = ExpColors.Ink, fontSize = 34.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
                    Text("Total Amount", color = ExpColors.InkSoft, fontSize = 12.sp)
                }
            }

            // Applied offer
            if (bestOffer != null) {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ExpColors.Gold.copy(alpha = 0.14f)),
                    elevation = expCardElevation(),
                    border = BorderStroke(1.5.dp, ExpColors.Gold),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(bestOffer.brand), contentAlignment = Alignment.Center) {
                            Text(bestOffer.provider.take(1), color = ExpColors.OnOlive, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(bestOffer.title, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("${bestOffer.provider} • You save ${savings.asInr()}", color = ExpColors.InkSoft, fontSize = 12.sp)
                        }
                        Text(
                            "Verified",
                            color = ExpColors.OnGold,
                            fontSize = 10.sp,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(ExpColors.Gold).padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }
            }

            // Final price
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Estimated Final Price", color = ExpColors.Ink, modifier = Modifier.weight(1f))
                Text(payable.asInr(), color = ExpColors.Ink, fontSize = 20.sp, fontFamily = ExpSerif, fontWeight = FontWeight.Bold)
            }

            // Budget impact
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Monthly Budget Impact", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 15.sp)
                    Row {
                        Text("${draft.category.label} • ${payable.asInr()}", color = ExpColors.InkSoft, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    }
                    Row {
                        Text("Remaining Balance", color = ExpColors.Ink, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text((budget.remaining - payable).asSignedInr(), color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            when (gate) {
                is ExpenseGate.Blocked -> {
                    StatusBanner(Icons.Outlined.Block, gate.message, ExpColors.Danger)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Override monthly limit", color = ExpColors.Ink, modifier = Modifier.weight(1f))
                        Switch(
                            checked = budget.overrideEnabled,
                            onCheckedChange = onOverrideChange,
                            colors = SwitchDefaults.colors(checkedTrackColor = ExpColors.Danger, checkedThumbColor = ExpColors.Cream),
                        )
                    }
                }
                is ExpenseGate.AllowedWithWarning -> StatusBanner(Icons.Outlined.Warning, gate.message, ExpColors.Warning)
                ExpenseGate.Allowed -> Unit
            }

            // UPI app
            Text("Pay with", color = ExpColors.Ink, fontFamily = ExpSerif, fontSize = 16.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                UpiApp.entries.forEach { u ->
                    val sel = u == app
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (sel) ExpColors.SandLight else ExpColors.Sand)
                            .border(if (sel) 2.dp else 1.dp, if (sel) ExpColors.Accent else ExpColors.Outline, RoundedCornerShape(16.dp))
                            .clickable { app = u }
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(Modifier.size(34.dp).clip(CircleShape).background(ExpColors.Butter), contentAlignment = Alignment.Center) {
                            Text(u.label.take(1), color = ExpColors.Green, fontWeight = FontWeight.Bold)
                        }
                        Text(u.label.substringBefore(" "), fontSize = 10.sp, color = ExpColors.Ink, maxLines = 1)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            PrimaryButton("Proceed to Pay", onClick = { onProceed(payable, app) }, enabled = !blocked)
            TextButton(
                onClick = { onLogOnly(payable) },
                enabled = !blocked,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Log only (already paid)", color = if (blocked) ExpColors.InkSoft else ExpColors.Accent) }
        }
    }
}

/* ---- Payment Success ---- */

@Composable
fun PaymentSuccessScreen(
    draft: ExpenseDraft,
    paidWith: UpiApp,
    reference: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    RouteScaffold(modifier) {
        Column(
            Modifier.weight(1f).fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.size(96.dp).clip(CircleShape).background(ExpColors.Butter), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CheckCircle, null, tint = ExpColors.Green, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Payment Successful!", color = ExpColors.Ink, fontSize = 24.sp, fontFamily = ExpSerif, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(draft.amount.asInr(), color = ExpColors.Ink, fontSize = 40.sp, fontFamily = ExpSerif, fontWeight = FontWeight.Bold)
            Text(draft.payee, color = ExpColors.Ink, fontSize = 16.sp)
            Text("${draft.dateTime.format(fullFmt)} • ${paidWith.label}", color = ExpColors.InkSoft, fontSize = 12.sp)
            Spacer(Modifier.height(24.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand), elevation = expCardElevation()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("UPI Ref. No.", color = ExpColors.InkSoft, fontSize = 11.sp)
                        Text(reference, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { clipboard.setText(AnnotatedString(reference)) }) {
                        Icon(Icons.Outlined.ContentCopy, "Copy reference", tint = ExpColors.Accent)
                    }
                }
            }
        }
        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PrimaryButton("Back to Home", onClick = onDone)
            OutlinedButton(
                onClick = { /* TODO receipt */ },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, ExpColors.Accent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpColors.Accent),
            ) { Text("View Receipt") }
        }
    }
}

/* ---- AI Money Coach ---- */

@Composable
fun CoachScreen(
    messages: List<CoachMessage>,
    onBack: () -> Unit,
    onAsk: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var input by remember { mutableStateOf("") }
    val suggestions = listOf("How much did I spend on food this week?", "Give me saving tips", "Show my budget status")

    RouteScaffold(modifier) {
        ScreenTopBar("AI Money Coach", onBack) {
            Icon(Icons.Outlined.AutoAwesome, null, tint = HeroGold, modifier = Modifier.padding(end = 12.dp))
        }
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(messages) { m ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.fromUser) Arrangement.End else Arrangement.Start) {
                    Text(
                        m.text,
                        color = if (m.fromUser) ExpColors.Green else ExpColors.Ink,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (m.fromUser) ExpColors.Butter else ExpColors.Sand)
                            .padding(14.dp),
                    )
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestions) { s ->
                Text(
                    s,
                    fontSize = 12.sp,
                    color = ExpColors.Accent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, ExpColors.Accent, RoundedCornerShape(50))
                        .clickable { onAsk(s) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Ask me anything...") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50),
                colors = fieldColors(),
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { if (input.isNotBlank()) { onAsk(input.trim()); input = "" } },
                modifier = Modifier.size(48.dp).clip(CircleShape).background(ExpColors.Butter),
            ) { Icon(Icons.AutoMirrored.Outlined.Send, "Send", tint = ExpColors.Green) }
        }
    }
}

/* ---- Edit limit ---- */



/* ---- Edit name ---- */

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
