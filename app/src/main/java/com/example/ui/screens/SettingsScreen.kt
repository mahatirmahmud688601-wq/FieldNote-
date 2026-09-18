package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calendar.CalendarSyncManager
import com.example.data.crypto.CryptoManager
import com.example.ui.FieldnoteUiState
import com.example.ui.components.ConfirmDialog
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun SettingsScreen(
    state: FieldnoteUiState,
    onToggleDarkTheme: (Boolean) -> Unit,
    onSetDefaultPriority: (String) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onTriggerTestNotification: () -> Unit,
    onExportData: ((String) -> Unit) -> Unit,
    onImportData: (String) -> Unit,
    onClearAllData: () -> Unit,
    onUpdatePassphrase: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var importInputText by remember { mutableStateOf("") }
    var showPassphraseDialog by remember { mutableStateOf(false) }
    var newPassphraseInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Settings & Privacy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                letterSpacing = (-0.4).sp
            )
            Text(
                text = "Preferences, encryption keys, and zero-knowledge data controls",
                fontSize = 12.sp,
                color = colors.textMuted
            )
        }

        // Appearance Group
        item {
            SettingsCard(title = "Appearance") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Interface Theme", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                        Text(
                            text = if (state.isDarkTheme) "Dark mode (charcoal #171815 & sage)" else "Warm light mode (#FAFAF7 off-white)",
                            fontSize = 11.5.sp,
                            color = colors.textMuted
                        )
                    }

                    OutlinedButton(
                        onClick = { onToggleDarkTheme(!state.isDarkTheme) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colors.surfaceMuted,
                            contentColor = colors.text
                        ),
                        border = null
                    ) {
                        Icon(
                            imageVector = if (state.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (state.isDarkTheme) "Light" else "Dark", fontSize = 12.sp)
                    }
                }
            }
        }

        // Tasks & Notifications Group
        item {
            SettingsCard(title = "Task Management") {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Default Priority", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Initial priority level for newly scheduled tasks", fontSize = 11.5.sp, color = colors.textMuted)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("low" to "Low", "medium" to "Med", "high" to "High").forEach { (key, label) ->
                                val isSelected = state.defaultPriority == key
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) colors.accentSoft else colors.surfaceMuted)
                                        .clickable { onSetDefaultPriority(key) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) colors.accent else colors.textMuted
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.border))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Real-Time Notifications", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Alarms and reminders for upcoming deadlines", fontSize = 11.5.sp, color = colors.textMuted)
                        }
                        Switch(
                            checked = state.notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = colors.accent
                            )
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.border))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Notification Test", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Dispatch a test deadline reminder to verify alerts", fontSize = 11.5.sp, color = colors.textMuted)
                        }
                        OutlinedButton(
                            onClick = onTriggerTestNotification,
                            shape = RoundedCornerShape(8.dp),
                            border = null,
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test", fontSize = 12.sp, color = colors.text)
                        }
                    }
                }
            }
        }

        // Calendar Integrations Group
        item {
            SettingsCard(title = "External Calendar Integrations") {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Google Calendar & Outlook", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Deep-link task commitments or sync with device calendars", fontSize = 11.5.sp, color = colors.textMuted)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                                    val sampleTask = state.tasks.firstOrNull { it.dueDate >= todayStr } ?: state.tasks.firstOrNull()
                                    if (sampleTask != null) {
                                        CalendarSyncManager.openGoogleCalendarWeb(context, sampleTask)
                                    } else {
                                        CalendarSyncManager.openCalendarAtDate(context, todayStr)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = null,
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                            ) {
                                Text("Google Cal", fontSize = 11.sp, color = colors.text)
                            }

                            OutlinedButton(
                                onClick = {
                                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                                    val sampleTask = state.tasks.firstOrNull { it.dueDate >= todayStr } ?: state.tasks.firstOrNull()
                                    if (sampleTask != null) {
                                        CalendarSyncManager.openOutlookCalendarWeb(context, sampleTask)
                                    } else {
                                        CalendarSyncManager.openCalendarAtDate(context, todayStr)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = null,
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                            ) {
                                Text("Outlook", fontSize = 11.sp, color = colors.text)
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.border))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Universal iCalendar (.ics)", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Export calendar feed importable by Apple, Google, and Outlook", fontSize = 11.5.sp, color = colors.textMuted)
                        }

                        OutlinedButton(
                            onClick = {
                                val ics = CalendarSyncManager.generateIcsString(state.tasks)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, ics)
                                    type = "text/calendar"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Export Fieldnote iCalendar (.ics)")
                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(shareIntent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = null,
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export .ics", fontSize = 11.5.sp, color = colors.text)
                        }
                    }
                }
            }
        }

        // Security & Privacy Vault Group
        item {
            SettingsCard(title = "End-to-End Encryption & Privacy Vault") {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = colors.accent, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "AES-256-GCM Vault", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = colors.text)
                                Text(text = "Hardware-accelerated Zero-Knowledge encryption active", fontSize = 11.5.sp, color = colors.textMuted)
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.border))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Master Passphrase", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Key: ${CryptoManager.getMasterPassphrasePreview()}", fontSize = 11.5.sp, color = colors.textMuted)
                        }
                        OutlinedButton(
                            onClick = { showPassphraseDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            border = null,
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change", fontSize = 12.sp, color = colors.text)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.border))

                    // Export / Import
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Data Vault Backup", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.text)
                            Text(text = "Export or restore encrypted JSON format", fontSize = 11.5.sp, color = colors.textMuted)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    onExportData { json ->
                                        exportedJsonText = json
                                        showExportDialog = true
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = null,
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Export", fontSize = 11.5.sp, color = colors.text)
                            }

                            OutlinedButton(
                                onClick = { showImportDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                border = null,
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.surfaceMuted)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Import", fontSize = 11.5.sp, color = colors.text)
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.border))

                    // Wipe data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Wipe All Data", fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = colors.danger)
                            Text(text = "Irreversibly delete all notes, tasks, and history", fontSize = 11.5.sp, color = colors.textMuted)
                        }
                        Button(
                            onClick = { showClearDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.danger)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // About Fieldnote
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceMuted)
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "Fieldnote • Version 1.0.0", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.text)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Minimalist Japanese stationery-inspired design system. Built with Kotlin & Jetpack Compose, Room SQLite offline persistence, and Google Gemini Copilot.",
                        fontSize = 11.5.sp,
                        color = colors.textMuted,
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Confirm Clear All Dialog
    ConfirmDialog(
        open = showClearDialog,
        title = "Wipe all Fieldnote data?",
        message = "This permanently removes all personal notes, tasks, and Copilot history from your local SQLite database. This action cannot be undone.",
        confirmLabel = "Wipe Everything",
        isDanger = true,
        onConfirm = {
            onClearAllData()
            showClearDialog = false
        },
        onCancel = { showClearDialog = false }
    )

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            shape = RoundedCornerShape(14.dp),
            containerColor = colors.surface,
            title = { Text("Encrypted Vault Export", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.text) },
            text = {
                Column {
                    Text("Your notes and tasks formatted as JSON:", fontSize = 12.sp, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportedJsonText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Done", color = Color.White)
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            shape = RoundedCornerShape(14.dp),
            containerColor = colors.surface,
            title = { Text("Import Data JSON", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.text) },
            text = {
                Column {
                    Text("Paste your previously exported Fieldnote JSON payload:", fontSize = 12.sp, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importInputText,
                        onValueChange = { importInputText = it },
                        placeholder = { Text("{\"notes\": [...], \"tasks\": [...]}") },
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importInputText.isNotBlank()) {
                            onImportData(importInputText)
                        }
                        showImportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Import Payload", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel", color = colors.textMuted)
                }
            }
        )
    }

    // Passphrase Dialog
    if (showPassphraseDialog) {
        AlertDialog(
            onDismissRequest = { showPassphraseDialog = false },
            shape = RoundedCornerShape(14.dp),
            containerColor = colors.surface,
            title = { Text("Set Master Passphrase", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.text) },
            text = {
                Column {
                    Text("Choose a secret passphrase used to derive your local AES-256 GCM encryption key.", fontSize = 12.sp, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassphraseInput,
                        onValueChange = { newPassphraseInput = it },
                        placeholder = { Text("Enter secret key phrase") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPassphraseInput.isNotBlank()) {
                            onUpdatePassphrase(newPassphraseInput)
                        }
                        showPassphraseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Save Key", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPassphraseDialog = false }) {
                    Text("Cancel", color = colors.textMuted)
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    val colors = FieldnoteTheme.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = colors.textMuted,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(24.dp))
        ) {
            content()
        }
    }
}
