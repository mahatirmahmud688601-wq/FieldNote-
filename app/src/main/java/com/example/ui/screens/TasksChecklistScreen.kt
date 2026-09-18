package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.FieldnoteUiState
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.FieldnoteBadge
import com.example.ui.components.FieldnoteTextField
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TasksChecklistScreen(
    state: FieldnoteUiState,
    onOpenTask: (TaskEntity?) -> Unit,
    onCycleStatus: (TaskEntity) -> Unit,
    onDeleteTask: (String) -> Unit,
    onSyncToCalendar: (TaskEntity) -> Unit,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onPriorityFilterChange: (String) -> Unit,
    onSortChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    var taskToDeleteId by remember { mutableStateOf<String?>(null) }
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    val filteredTasks = remember(
        state.tasks,
        state.searchQuery,
        state.taskStatusFilter,
        state.taskPriorityFilter,
        state.tasksSortBy
    ) {
        var list = state.tasks
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.tags.lowercase().contains(q)
            }
        }
        if (state.taskStatusFilter != "all") {
            list = list.filter { it.status == state.taskStatusFilter }
        }
        if (state.taskPriorityFilter != "all") {
            list = list.filter { it.priority == state.taskPriorityFilter }
        }
        when (state.tasksSortBy) {
            "due" -> list.sortedBy { if (it.dueDate.isBlank()) "9999-99-99" else it.dueDate }
            "priority" -> list.sortedByDescending {
                when (it.priority) {
                    "high" -> 3
                    "medium" -> 2
                    else -> 1
                }
            }
            else -> list.sortedByDescending { it.createdAt }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title and + Task Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tasks Checklist",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    letterSpacing = (-0.4).sp
                )
                Text(
                    text = "${filteredTasks.count { it.status != "completed" }} pending, ${filteredTasks.count { it.status == "completed" }} done",
                    fontSize = 12.sp,
                    color = colors.textMuted
                )
            }

            Button(
                onClick = { onOpenTask(null) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("checklist_new_task_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Task", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        FieldnoteTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Search tasks...",
            leadingIcon = Icons.Default.Search,
            testTag = "tasks_search_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips horizontal scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskFilterPill(
                label = "All Statuses",
                selected = state.taskStatusFilter == "all",
                onClick = { onStatusFilterChange("all") }
            )
            TaskFilterPill(
                label = "To Do",
                selected = state.taskStatusFilter == "todo",
                onClick = { onStatusFilterChange("todo") }
            )
            TaskFilterPill(
                label = "In Progress",
                selected = state.taskStatusFilter == "in_progress",
                onClick = { onStatusFilterChange("in_progress") }
            )
            TaskFilterPill(
                label = "Completed",
                selected = state.taskStatusFilter == "completed",
                onClick = { onStatusFilterChange("completed") }
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(colors.border)
            )

            TaskFilterPill(
                label = "All Priorities",
                selected = state.taskPriorityFilter == "all",
                onClick = { onPriorityFilterChange("all") }
            )
            TaskFilterPill(
                label = "High",
                selected = state.taskPriorityFilter == "high",
                onClick = { onPriorityFilterChange("high") }
            )
            TaskFilterPill(
                label = "Medium",
                selected = state.taskPriorityFilter == "medium",
                onClick = { onPriorityFilterChange("medium") }
            )
            TaskFilterPill(
                label = "Low",
                selected = state.taskPriorityFilter == "low",
                onClick = { onPriorityFilterChange("low") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No tasks match your filters.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap + New Task to create your next commitment.",
                        fontSize = 12.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskChecklistItem(
                        task = task,
                        todayStr = todayStr,
                        onOpen = { onOpenTask(task) },
                        onCycleStatus = { onCycleStatus(task) },
                        onSyncCalendar = { onSyncToCalendar(task) },
                        onDelete = { taskToDeleteId = task.id }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    ConfirmDialog(
        open = taskToDeleteId != null,
        title = "Delete task?",
        message = "This will permanently remove the task from your checklist.",
        confirmLabel = "Delete",
        isDanger = true,
        onConfirm = {
            taskToDeleteId?.let { onDeleteTask(it) }
            taskToDeleteId = null
        },
        onCancel = { taskToDeleteId = null }
    )
}

@Composable
private fun TaskFilterPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = FieldnoteTheme.colors
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) colors.accent else colors.surface)
            .border(1.dp, if (selected) colors.accent else colors.border, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.8.sp,
            color = if (selected) Color.White else colors.textMuted
        )
    }
}

@Composable
fun TaskChecklistItem(
    task: TaskEntity,
    todayStr: String,
    onOpen: () -> Unit,
    onCycleStatus: () -> Unit,
    onSyncCalendar: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val isCompleted = task.status == "completed"
    val isInProgress = task.status == "in_progress"
    val isOverdue = task.dueDate.isNotBlank() && task.dueDate < todayStr && !isCompleted
    val priorityColor = when (task.priority.lowercase()) {
        "high" -> colors.danger
        "medium" -> colors.amber
        else -> colors.accent
    }

    // Floating checklist card featuring soft blurred glassmorphism effects
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, if (isOverdue) colors.danger.copy(alpha = 0.5f) else colors.border, RoundedCornerShape(20.dp))
            .clickable { onOpen() }
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Crisp Circular Check Toggle: Todo -> In Progress -> Done
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .then(
                        when {
                            isCompleted -> Modifier.background(colors.accent)
                            isInProgress -> Modifier.background(colors.amberSoft).border(1.5.dp, colors.amber, CircleShape)
                            else -> Modifier.border(1.5.dp, colors.accent.copy(alpha = 0.6f), CircleShape)
                        }
                    )
                    .clickable { onCycleStatus() },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isCompleted -> Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    isInProgress -> Icon(
                        imageVector = Icons.Default.Pending,
                        contentDescription = "In Progress",
                        tint = colors.amber,
                        modifier = Modifier.size(16.dp)
                    )
                    else -> {} // Crisp clean empty circle
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Priority Dot Indicator
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(priorityColor)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = colors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Subtle timeline marker
                if (task.dueTime.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⏱ ${task.dueTime}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.accent
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Due date badge
            if (task.dueDate.isNotBlank()) {
                val dueText = if (task.dueDate == todayStr) "Today" else task.dueDate.takeLast(5)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isOverdue) colors.dangerSoft else colors.surfaceMuted)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    if (isOverdue) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Overdue",
                            tint = colors.danger,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Text(
                        text = dueText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isOverdue) colors.danger else colors.textMuted
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Priority Pill
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

            Spacer(modifier = Modifier.width(4.dp))

            // Calendar Sync Action
            IconButton(
                onClick = onSyncCalendar,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Sync to Calendar",
                    tint = if (task.syncedWithCalendar) colors.accent else colors.textMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(15.dp)
                )
            }

            // Delete
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colors.danger.copy(alpha = 0.7f),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
