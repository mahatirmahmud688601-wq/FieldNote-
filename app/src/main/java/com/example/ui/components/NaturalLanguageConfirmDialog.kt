package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.nlp.ParsedTaskResult
import com.example.ui.theme.FieldnoteTheme

@Composable
fun NaturalLanguageConfirmDialog(
    parsedResult: ParsedTaskResult,
    onConfirm: (syncToCalendar: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = FieldnoteTheme.colors
    var syncToCalendar by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirm Quick Add Task",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Fieldnote interpreted your input into structured schedule fields:",
                    fontSize = 12.sp,
                    color = colors.textMuted
                )

                // Parsed Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = parsedResult.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            InfoBadge("PRIORITY: ${parsedResult.priority.uppercase()}", colors.accent)
                            InfoBadge(parsedResult.category.uppercase(), colors.blue)
                            InfoBadge("${parsedResult.estimatedMinutes}m", colors.amber)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = colors.textMuted, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Date: ${parsedResult.dueDate} at ${parsedResult.dueTime}",
                                fontSize = 12.sp,
                                color = colors.textMuted
                            )
                        }
                    }
                }

                // Device Calendar Sync Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = syncToCalendar,
                        onCheckedChange = { syncToCalendar = it },
                        colors = CheckboxDefaults.colors(checkedColor = colors.accent)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sync to Google Calendar / Outlook",
                        fontSize = 12.sp,
                        color = colors.text
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(syncToCalendar) },
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                shape = CircleShape,
                modifier = Modifier.testTag("confirm_nl_task_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule Task", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = CircleShape
            ) {
                Text("Cancel", color = colors.textMuted)
            }
        },
        containerColor = colors.bg
    )
}

@Composable
private fun InfoBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
