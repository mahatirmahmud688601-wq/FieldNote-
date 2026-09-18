package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.crypto.CryptoManager
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import com.example.ui.FieldnoteScreen
import com.example.ui.FieldnoteUiState
import com.example.ui.components.FieldnoteBadge
import com.example.ui.components.StatCard
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(
    state: FieldnoteUiState,
    onNavigate: (FieldnoteScreen) -> Unit,
    onOpenNote: (NoteEntity?) -> Unit,
    onOpenTask: (TaskEntity?) -> Unit,
    onCycleTaskStatus: (TaskEntity) -> Unit,
    onToggleCopilot: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    val shortToday = SimpleDateFormat("MMM d", Locale.getDefault()).format(Calendar.getInstance().time)

    val activeNotes = state.notes.filter { !it.archived }
    val pendingTasks = state.tasks.filter { it.status != "completed" }
    val completedTasks = state.tasks.filter { it.status == "completed" }
    val overdueTasks = pendingTasks.filter { it.dueDate.isNotBlank() && it.dueDate < todayStr }
    val todaysTasks = state.tasks.filter { it.dueDate == todayStr }
    val priorityTasks = pendingTasks.sortedByDescending { if (it.priority == "high") 2 else 1 }.take(3)
    val recentNotes = activeNotes.sortedByDescending { it.updatedAt }.take(4)

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val periodWord = when {
        hour < 12 -> "this morning"
        hour < 17 -> "this afternoon"
        else -> "this evening"
    }
    val greetingWord = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Hello"
        else -> "Good evening"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // BENTO GRID HERO TOP CARD (Minimalist Hero with 75% Progress Ring Meter)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(32.dp))
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Category / Date uppercase tag
                        Text(
                            text = "TODAY • $shortToday".uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp,
                            color = colors.accent
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Stylized Bold Greeting Headline
                        val headline = buildAnnotatedString {
                            append("$greetingWord. You have ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal)) {
                                append("${pendingTasks.size} key tasks")
                            }
                            append(" for $periodWord.")
                        }

                        Text(
                            text = headline,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            fontStyle = FontStyle.Italic,
                            color = colors.text,
                            lineHeight = 28.sp,
                            letterSpacing = (-0.3).sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Clean Primary Action Pill Button
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onToggleCopilot() },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.accent,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.testTag("dashboard_plan_day_button")
                            ) {
                                Text(
                                    text = "Plan My Day",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.2.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { onNavigate(FieldnoteScreen.CALENDAR) },
                                shape = CircleShape,
                                border = BorderStroke(1.dp, colors.border.copy(alpha = 0.8f)),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = colors.text
                                ),
                                modifier = Modifier.testTag("dashboard_view_calendar_button")
                            ) {
                                Text(
                                    text = "Calendar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Today's Progress Ring Meter (75% completed)
                    Box(
                        modifier = Modifier.size(78.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 7.dp.toPx()
                            // Track
                            drawCircle(
                                color = colors.border,
                                style = Stroke(width = strokeWidth)
                            )
                            // 75% Arc Progress
                            drawArc(
                                color = colors.accent,
                                startAngle = -90f,
                                sweepAngle = 270f, // 75% completed
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "75%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.text,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "DONE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colors.accent
                            )
                        }
                    }
                }
            }
        }

        // GEOMETRIC BENTO SPLIT GRID (Priority Tasks + Timeline)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Bento: Floating Checklist Tasks Card (rounded 28dp)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(28.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TASKS",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colors.text.copy(alpha = 0.6f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .clickable { onOpenTask(null) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add task",
                                    tint = colors.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (priorityTasks.isEmpty()) {
                            Text(
                                text = "No priority tasks.",
                                fontSize = 12.sp,
                                color = colors.textMuted,
                                fontStyle = FontStyle.Italic
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                priorityTasks.forEach { task ->
                                    val isDone = task.status == "completed"
                                    val priorityDotColor = when (task.priority.lowercase()) {
                                        "high" -> colors.danger
                                        "medium" -> colors.amber
                                        else -> colors.accent
                                    }
                                    // Floating checklist card featuring soft blurred glassmorphism effects
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(colors.surfaceMuted.copy(alpha = 0.75f))
                                            .border(1.dp, colors.border.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                                            .clickable { onOpenTask(task) }
                                            .padding(horizontal = 9.dp, vertical = 7.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Crisp circular check toggle
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .then(
                                                        if (isDone) {
                                                            Modifier.background(colors.accent)
                                                        } else {
                                                            Modifier.border(1.5.dp, colors.accent, CircleShape)
                                                        }
                                                    )
                                                    .clickable { onCycleTaskStatus(task) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isDone) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(7.dp))

                                            // Priority dot indicator
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(priorityDotColor)
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = task.title,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isDone) colors.textMuted else colors.text,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                                )
                                                // Subtle timeline marker
                                                if (task.dueTime.isNotBlank()) {
                                                    Text(
                                                        text = "• ${task.dueTime}",
                                                        fontSize = 9.5.sp,
                                                        color = colors.accent.copy(alpha = 0.8f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Bento: Live Timeline Card (accent container with rounded 28dp)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(colors.accentSoft)
                        .border(1.dp, colors.accentBorder, RoundedCornerShape(28.dp))
                        .clickable { onNavigate(FieldnoteScreen.CALENDAR) }
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TIMELINE",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colors.accent
                            )
                            Text(
                                text = "Live",
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Italic,
                                color = colors.accent.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val scheduledTasks = state.tasks.filter { it.dueTime.isNotBlank() }.take(3)
                        if (scheduledTasks.isEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                TimelineEventItem(time = "09:00", title = "Morning Review", isLive = true)
                                TimelineEventItem(time = "14:00", title = "Focus Block", isLive = false)
                                TimelineEventItem(time = "17:30", title = "Daily Reflection", isLive = false)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                scheduledTasks.forEachIndexed { index, t ->
                                    TimelineEventItem(
                                        time = t.dueTime,
                                        title = t.title,
                                        isLive = index == 0
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // GEOMETRIC CONTRAST COPILOT PILL BAR
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(colors.pillBg)
                    .border(1.dp, colors.border, CircleShape)
                    .clickable { onToggleCopilot() }
                    .padding(horizontal = 6.dp, vertical = 5.dp)
                    .testTag("copilot_quick_bar")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Copilot Voice",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Ask Copilot anything...",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.pillText.copy(alpha = 0.65f),
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.pillText.copy(alpha = 0.08f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⌘ K",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.pillText.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // 4 Stat Cards in 2x2 Grid (Geometric Balanced)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Notes",
                        value = "${activeNotes.size}",
                        accentColor = colors.blue,
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(FieldnoteScreen.NOTES) }
                    )
                    StatCard(
                        title = "Pending Tasks",
                        value = "${pendingTasks.size}",
                        accentColor = colors.amber,
                        icon = Icons.Default.HourglassTop,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(FieldnoteScreen.TASKS) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Completed",
                        value = "${completedTasks.size}",
                        accentColor = colors.accent,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(FieldnoteScreen.TASKS) }
                    )
                    StatCard(
                        title = "Overdue",
                        value = "${overdueTasks.size}",
                        accentColor = colors.danger,
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(FieldnoteScreen.CALENDAR) }
                    )
                }
            }
        }

        // Quick Modules Bar (Focus, Habits, Analytics, Vault)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickModulePill(
                    title = "Focus",
                    icon = Icons.Default.HourglassBottom,
                    accent = colors.accent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(FieldnoteScreen.FOCUS) }
                )
                QuickModulePill(
                    title = "Habits",
                    icon = Icons.Default.LocalFireDepartment,
                    accent = colors.amber,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(FieldnoteScreen.HABITS) }
                )
                QuickModulePill(
                    title = "Analytics",
                    icon = Icons.Default.TrendingUp,
                    accent = colors.blue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(FieldnoteScreen.ANALYTICS) }
                )
                QuickModulePill(
                    title = "Vault",
                    icon = Icons.Default.Shield,
                    accent = Color(0xFFC7634C),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(FieldnoteScreen.VAULT) }
                )
            }
        }

        // Today's Tasks Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TODAY'S SCHEDULE (${todaysTasks.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.text.copy(alpha = 0.7f)
                )
                TextButton(onClick = { onNavigate(FieldnoteScreen.TASKS) }) {
                    Text("See all", fontSize = 12.sp, color = colors.accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (todaysTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks scheduled for today. Runway is clear.",
                        fontSize = 12.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            items(todaysTasks) { task ->
                DashboardTaskRow(
                    task = task,
                    onOpen = { onOpenTask(task) },
                    onToggleStatus = { onCycleTaskStatus(task) }
                )
            }
        }

        // Recent Notes Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT NOTES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = colors.text.copy(alpha = 0.7f)
                )
                TextButton(onClick = { onNavigate(FieldnoteScreen.NOTES) }) {
                    Text("See all", fontSize = 12.sp, color = colors.accent, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (recentNotes.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No notes created yet. Capture your first idea.",
                        fontSize = 12.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            items(recentNotes) { note ->
                DashboardNoteRow(
                    note = note,
                    onOpen = { onOpenNote(note) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TimelineEventItem(
    time: String,
    title: String,
    isLive: Boolean
) {
    val colors = FieldnoteTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical indicator line
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(28.dp)
                .background(if (isLive) colors.accent else colors.text.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = time,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLive) colors.accent else colors.text.copy(alpha = 0.5f),
                letterSpacing = 0.5.sp
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isLive) colors.text else colors.text.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DashboardTaskRow(
    task: TaskEntity,
    onOpen: () -> Unit,
    onToggleStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val isCompleted = task.status == "completed"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
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
                    .clickable { onToggleStatus() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.dueTime.isNotBlank()) {
                    Text(
                        text = "At ${task.dueTime}",
                        fontSize = 11.sp,
                        color = colors.textMuted
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            val priorityColor = when (task.priority) {
                "high" -> colors.danger
                "medium" -> colors.amber
                else -> colors.textMuted
            }
            FieldnoteBadge(
                text = task.priority.uppercase(),
                backgroundColor = priorityColor.copy(alpha = 0.15f),
                contentColor = priorityColor
            )
        }
    }
}

@Composable
fun DashboardNoteRow(
    note: NoteEntity,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val decryptedContent = if (note.encrypted) CryptoManager.decrypt(note.content) else note.content

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .clickable { onOpen() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = note.title.ifBlank { "Untitled Note" },
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (note.pinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Pin,
                            contentDescription = "Pinned",
                            tint = colors.accent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    if (note.encrypted) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = colors.amber,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = decryptedContent.take(70).ifBlank { "No content" },
                    fontSize = 12.sp,
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickModulePill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.text)
        }
    }
}

