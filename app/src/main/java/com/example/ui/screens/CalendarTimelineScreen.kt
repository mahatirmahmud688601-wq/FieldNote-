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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calendar.CalendarSyncManager
import com.example.data.model.TaskEntity
import com.example.ui.FieldnoteUiState
import com.example.ui.components.FieldnoteBadge
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarTimelineScreen(
    state: FieldnoteUiState,
    onOpenTask: (TaskEntity) -> Unit,
    onCycleStatus: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val context = LocalContext.current
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    // Group tasks with due dates
    val datedTasks = remember(state.tasks) {
        state.tasks.filter { it.dueDate.isNotBlank() }
    }

    val overdueTasks = remember(datedTasks, todayStr) {
        datedTasks.filter { it.dueDate < todayStr && it.status != "completed" }
            .sortedBy { it.dueDate }
    }

    val upcomingGrouped = remember(datedTasks, todayStr) {
        val map = sortedMapOf<String, MutableList<TaskEntity>>()
        datedTasks.filter { it.dueDate >= todayStr }.forEach { task ->
            map.getOrOrPut(task.dueDate) { mutableListOf() }.add(task)
        }
        map.entries.toList()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Calendar Timeline",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "Synchronized deadlines & upcoming schedule",
                        fontSize = 12.sp,
                        color = colors.textMuted
                    )
                }

                Button(
                    onClick = { CalendarSyncManager.openCalendarAtDate(context, todayStr) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.surfaceMuted,
                        contentColor = colors.text
                    )
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Device App", fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Seamless Calendar Sync Suite (Google Calendar & Outlook Calendar)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colors.accentSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = colors.accent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CALENDAR INTEGRATIONS",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = colors.accent
                            )
                        }

                        Text(
                            text = "Live Sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.accent
                        )
                    }

                    Text(
                        text = "Seamless two-way integration with Google Calendar and Outlook. Sync upcoming deadlines or export universal .ics format.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = colors.textMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Google Calendar Button
                        Button(
                            onClick = {
                                val firstUpcoming = datedTasks.firstOrNull { it.dueDate >= todayStr } ?: datedTasks.firstOrNull()
                                if (firstUpcoming != null) {
                                    CalendarSyncManager.openGoogleCalendarWeb(context, firstUpcoming)
                                } else {
                                    CalendarSyncManager.openCalendarAtDate(context, todayStr)
                                }
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceMuted,
                                contentColor = colors.text
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Google Cal", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Outlook Calendar Button
                        Button(
                            onClick = {
                                val firstUpcoming = datedTasks.firstOrNull { it.dueDate >= todayStr } ?: datedTasks.firstOrNull()
                                if (firstUpcoming != null) {
                                    CalendarSyncManager.openOutlookCalendarWeb(context, firstUpcoming)
                                } else {
                                    CalendarSyncManager.openCalendarAtDate(context, todayStr)
                                }
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceMuted,
                                contentColor = colors.text
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Outlook", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Export .ICS
                        Button(
                            onClick = {
                                val ics = CalendarSyncManager.generateIcsString(datedTasks)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, ics)
                                    type = "text/calendar"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Export Fieldnote iCalendar (.ics)")
                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(shareIntent)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.accent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export .ICS", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Overdue Alert Section
        if (overdueTasks.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Overdue",
                            tint = colors.danger,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Overdue Deadlines (${overdueTasks.size})",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.danger
                        )
                    }

                    overdueTasks.forEach { task ->
                        TimelineTaskCard(
                            task = task,
                            isOverdue = true,
                            onOpen = { onOpenTask(task) },
                            onCycleStatus = { onCycleStatus(task) }
                        )
                    }
                }
            }
        }

        // Upcoming Timeline
        if (upcomingGrouped.isEmpty() && overdueTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No scheduled timeline events.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.text
                        )
                        Text(
                            text = "Tasks with due dates will automatically align here.",
                            fontSize = 12.5.sp,
                            color = colors.textMuted
                        )
                    }
                }
            }
        } else {
            items(upcomingGrouped, key = { it.key }) { (dateStr, tasks) ->
                val isToday = dateStr == todayStr
                val dateLabel = if (isToday) "Today" else formatDateForTimeline(dateStr)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date indicator column
                    Column(
                        modifier = Modifier.width(68.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = dateLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (isToday) colors.accent else colors.text
                        )
                        Text(
                            text = dateStr.takeLast(5),
                            fontSize = 10.sp,
                            color = colors.textMuted
                        )
                    }

                    // Vertical rule + tasks
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 1.dp,
                                color = if (isToday) colors.accent.copy(alpha = 0.3f) else colors.border,
                                shape = RoundedCornerShape(0.dp)
                            )
                            .padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tasks.forEach { task ->
                            TimelineTaskCard(
                                task = task,
                                isOverdue = false,
                                onOpen = { onOpenTask(task) },
                                onCycleStatus = { onCycleStatus(task) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun TimelineTaskCard(
    task: TaskEntity,
    isOverdue: Boolean,
    onOpen: () -> Unit,
    onCycleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val isCompleted = task.status == "completed"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(
                1.dp,
                if (isOverdue) colors.danger.copy(alpha = 0.5f) else colors.border,
                RoundedCornerShape(20.dp)
            )
            .clickable { onOpen() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .then(
                        if (isCompleted) {
                            Modifier.background(colors.accent)
                        } else {
                            Modifier.border(2.dp, colors.accent, CircleShape)
                        }
                    )
                    .clickable { onCycleStatus() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.text,
                    maxLines = 1
                )
                if (task.dueTime.isNotBlank()) {
                    Text(
                        text = "Time: ${task.dueTime}",
                        fontSize = 11.sp,
                        color = colors.textMuted
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            FieldnoteBadge(
                text = task.priority.uppercase(),
                backgroundColor = when (task.priority) {
                    "high" -> colors.danger.copy(alpha = 0.15f)
                    "medium" -> colors.amber.copy(alpha = 0.15f)
                    else -> colors.accentSoft
                },
                contentColor = when (task.priority) {
                    "high" -> colors.danger
                    "medium" -> colors.amber
                    else -> colors.accent
                }
            )
        }
    }
}

private fun formatDateForTimeline(dateStr: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr)
        if (date != null) {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun <K, V> java.util.SortedMap<K, V>.getOrOrPut(key: K, defaultValue: () -> V): V {
    return this[key] ?: defaultValue().also { this[key] = it }
}
