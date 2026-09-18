package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.TaskEntity
import com.example.ui.components.FieldnoteTextField
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSheetModal(
    task: TaskEntity?,
    defaultPriority: String,
    onSave: (TaskEntity, Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val isNew = task == null || task.title.isBlank()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var priority by remember { mutableStateOf(task?.priority ?: defaultPriority) }
    var status by remember { mutableStateOf(task?.status ?: "todo") }
    var dueDate by remember { mutableStateOf(task?.dueDate?.ifBlank { todayStr } ?: todayStr) }
    var dueTime by remember { mutableStateOf(task?.dueTime?.ifBlank { "14:00" } ?: "14:00") }
    var tagsInput by remember { mutableStateOf(task?.tags ?: "") }
    var syncCalendar by remember { mutableStateOf(task?.syncedWithCalendar ?: true) }
    var hasError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = colors.surface,
        dragHandle = null
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isNew) "New Task" else "Edit Task",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    letterSpacing = (-0.3).sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.textMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            FieldnoteTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) hasError = false
                },
                placeholder = "Task title (e.g. Finish Physics next Friday at 4 PM)",
                testTag = "task_sheet_title_input"
            )
            if (hasError) {
                Text(
                    text = "Please enter a task title.",
                    color = colors.danger,
                    fontSize = 11.5.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            // Quick Auto-Parse Chip
            if (title.contains("at ") || title.contains("pm") || title.contains("am") || title.contains("tomorrow") || title.contains("next") || title.contains("priority")) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.accent.copy(alpha = 0.15f))
                        .border(1.dp, colors.accent.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            val parsed = com.example.data.nlp.NaturalLanguageTaskParser.parse(title)
                            title = parsed.title
                            priority = parsed.priority
                            if (parsed.dueDate.isNotBlank()) dueDate = parsed.dueDate
                            if (parsed.dueTime.isNotBlank()) dueTime = parsed.dueTime
                            if (parsed.category.isNotBlank() && tagsInput.isBlank()) tagsInput = parsed.category
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✨ Auto-detect schedule & priority from text",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accent
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Description or agenda notes...", color = colors.textMuted, fontSize = 13.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.border,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedTextColor = colors.text,
                    unfocusedTextColor = colors.text
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Priority Selection
            Text(text = "PRIORITY LEVEL", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = colors.textMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("low" to "Low", "medium" to "Medium", "high" to "High").forEach { (valKey, label) ->
                    val isSelected = priority == valKey
                    val chipColor = when (valKey) {
                        "high" -> colors.danger
                        "medium" -> colors.amber
                        else -> colors.accent
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .background(if (isSelected) chipColor.copy(alpha = 0.15f) else colors.surfaceMuted)
                            .border(1.dp, if (isSelected) chipColor else colors.border, CircleShape)
                            .clickable { priority = valKey }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.6.sp,
                            color = if (isSelected) chipColor else colors.textMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Due Date & Time
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Due Date (YYYY-MM-DD)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    FieldnoteTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        placeholder = "YYYY-MM-DD"
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Due Time (HH:MM)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.textMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    FieldnoteTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        placeholder = "14:00"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tags
            Text(text = "Tags", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colors.textMuted)
            Spacer(modifier = Modifier.height(6.dp))
            FieldnoteTextField(
                value = tagsInput,
                onValueChange = { tagsInput = it },
                placeholder = "Comma separated (e.g. Sprint, Design, Marketing)"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sync to Calendar Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.surfaceMuted)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Sync to Device Calendar", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.text)
                    Text(text = "Creates an Android calendar event automatically", fontSize = 11.sp, color = colors.textMuted)
                }
                Switch(
                    checked = syncCalendar,
                    onCheckedChange = { syncCalendar = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.accent
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = colors.textMuted, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            hasError = true
                            return@Button
                        }
                        val finalTask = (task ?: TaskEntity(
                            id = UUID.randomUUID().toString(),
                            title = title
                        )).copy(
                            title = title.trim(),
                            description = description.trim(),
                            priority = priority,
                            status = status,
                            dueDate = dueDate.trim(),
                            dueTime = dueTime.trim(),
                            tags = tagsInput.trim(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(finalTask, syncCalendar)
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("task_sheet_confirm_button")
                ) {
                    Text(if (isNew) "Create Task" else "Save Changes", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
