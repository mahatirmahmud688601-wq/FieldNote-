package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitEntity
import com.example.ui.FieldnoteUiState
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HabitsRoutinesScreen(
    state: FieldnoteUiState,
    onToggleHabit: (HabitEntity) -> Unit,
    onAddHabit: (String, String, String, Int, String) -> Unit,
    onDeleteHabit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    var showAddDialog by remember { mutableStateOf(false) }
    var habitTitle by remember { mutableStateOf("") }
    var habitCategory by remember { mutableStateOf("Productivity") }
    var habitFrequency by remember { mutableStateOf("daily") }

    val completedCount = state.habits.count { it.completedDates.contains(todayStr) }
    val totalCount = state.habits.size
    val completionRate = if (totalCount > 0) (completedCount.toFloat() / totalCount * 100).toInt() else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "Habits & Routines",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Consistent daily rituals build lasting craft",
                        fontSize = 13.sp,
                        color = colors.textMuted
                    )
                }

                // Add Habit Button
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colors.accent)
                        .clickable { showAddDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("add_habit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Habit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }

        // Streak & Progress Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "TODAY'S COMPLETION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedCount of $totalCount Done ($completionRate%)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (completionRate == 100) "All rituals achieved. Outstanding momentum." else "Keep the chain unbroken today.",
                            fontSize = 12.sp,
                            color = colors.textMuted
                        )
                    }

                    // Flame Streak Icon Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(colors.accent.copy(alpha = 0.15f))
                            .border(1.dp, colors.accent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Habit Streak",
                            tint = colors.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        if (state.habits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active habits yet. Tap '+ New Habit' to begin.",
                        fontSize = 13.sp,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            items(state.habits) { habit ->
                val isCompletedToday = habit.completedDates.contains(todayStr)
                HabitRowItem(
                    habit = habit,
                    isCompletedToday = isCompletedToday,
                    onToggle = { onToggleHabit(habit) },
                    onDelete = { onDeleteHabit(habit.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create New Routine", fontWeight = FontWeight.Bold, color = colors.text) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = habitTitle,
                        onValueChange = { habitTitle = it },
                        label = { Text("Habit Title") },
                        placeholder = { Text("e.g. Read 20 pages") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = habitCategory,
                        onValueChange = { habitCategory = it },
                        label = { Text("Category") },
                        placeholder = { Text("Productivity, Health, Study") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (habitTitle.isNotBlank()) {
                            onAddHabit(habitTitle, habitCategory, habitFrequency, 7, "#8FA378")
                            habitTitle = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("Save Routine", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = colors.textMuted)
                }
            },
            containerColor = colors.surface
        )
    }
}

@Composable
private fun HabitRowItem(
    habit: HabitEntity,
    isCompletedToday: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = FieldnoteTheme.colors
    val itemColor = try {
        Color(android.graphics.Color.parseColor(habit.color))
    } catch (e: Exception) {
        colors.accent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, if (isCompletedToday) itemColor.copy(alpha = 0.5f) else colors.border, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Check Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCompletedToday) itemColor else Color.Transparent)
                    .border(2.dp, itemColor, CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isCompletedToday) {
                    Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(itemColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(habit.category.uppercase(), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = itemColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔥 ${habit.streak} day streak (Best: ${habit.bestStreak})",
                        fontSize = 11.5.sp,
                        color = colors.textMuted
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Habit", tint = colors.textMuted.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
