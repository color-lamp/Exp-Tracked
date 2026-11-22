@file:Suppress("UnsafeOptInUsageError")

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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
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
import androidx.activity.compose.rememberLauncherForActivityResult
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
fun PreferredUpiAppDialog(
    currentPackage: String?,
    onDismiss: () -> Unit,
    onSelect: (String?) -> Unit,
) {
    val options = listOf(
        null to "None (system chooser)",
        "com.google.android.apps.nbu.paisa.user" to "Google Pay",
        "com.phonepe.app" to "PhonePe",
        "net.one97.paytm" to "Paytm",
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ExpColors.SandLight,
        title = { Text("Preferred UPI App", fontFamily = ExpSerif) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                options.forEach { (pkg, label) ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onSelect(pkg) }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = currentPackage == pkg, onClick = { onSelect(pkg) })
                        Spacer(Modifier.width(8.dp))
                        Text(label, color = ExpColors.Ink)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraPreview(onResult: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    if (!hasPermission) {
        Box(Modifier.fillMaxSize().background(ExpColors.OliveDark), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission required", color = ExpColors.OnOlive, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) }) {
                    Text("Grant")
                }
            }
        }
        return
    }

    val previewView = remember { androidx.camera.view.PreviewView(context) }
    var scanned by remember { mutableStateOf(false) }
    val cameraProviderFuture = remember { androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context) }

    val galleryLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            runCatching {
                val inputImage = com.google.mlkit.vision.common.InputImage.fromFilePath(context, it)
                com.google.mlkit.vision.barcode.BarcodeScanning.getClient(
                    com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                        .build()
                ).process(inputImage).addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val raw = barcode.rawValue
                        if (!raw.isNullOrBlank()) { onResult(raw); break }
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        Button(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) {
            Text("Import from gallery")
        }
    }

    DisposableEffect(lifecycleOwner, scanned) {
        val executor = androidx.core.content.ContextCompat.getMainExecutor(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = androidx.camera.core.ImageAnalysis.Builder()
                .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val scanner = com.google.mlkit.vision.barcode.BarcodeScanning.getClient(
                com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE)
                    .build()
            )
            analysis.setAnalyzer(executor, object : androidx.camera.core.ImageAnalysis.Analyzer {
                @OptIn(androidx.camera.core.ExperimentalGetImage::class)
                override fun analyze(imageProxy: androidx.camera.core.ImageProxy) {
                    if (scanned) {
                        imageProxy.close()
                        return
                    }
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = com.google.mlkit.vision.common.InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        scanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val raw = barcode.rawValue
                                    if (!raw.isNullOrBlank() && !scanned) {
                                        scanned = true
                                        analysis.clearAnalyzer()
                                        onResult(raw)
                                        break
                                    }
                                }
                            }
                            .addOnCompleteListener { imageProxy.close() }
                    } else {
                        imageProxy.close()
                    }
                }
            })
            val selector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
            } catch (e: Exception) {
                // ignore binding errors
            }
        }, executor)

        onDispose {
            runCatching { cameraProviderFuture.get().unbindAll() }
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
