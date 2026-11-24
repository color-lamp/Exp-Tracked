@file:OptIn(ExperimentalMaterial3Api::class)

package com.exptracked.ui.screens

import com.exptracked.ui.model.*
import com.exptracked.ui.theme.*
import com.exptracked.ui.components.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun NotificationsScreen(notifications: List<AppNotification>, onBack: () -> Unit) {
    val fmt = DateTimeFormatter.ofPattern("d MMM, hh:mm a")
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Notifications", onBack)
        if (notifications.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications in the last 7 days", color = ExpColors.InkSoft, fontSize = 14.sp)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(notifications.sortedByDescending { it.timestamp }) { n ->
                    val color = when (n.type) {
                        "budget" -> ExpColors.Danger
                        "payment" -> Color(0xFF4CAF50)
                        else -> Color(0xFF2196F3)
                    }
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ExpColors.Sand)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(color))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(n.title, color = ExpColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(n.message, color = ExpColors.InkSoft, fontSize = 12.sp)
                                val ts = Instant.ofEpochMilli(n.timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
                                Text(ts.format(fmt), color = ExpColors.InkSoft, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}