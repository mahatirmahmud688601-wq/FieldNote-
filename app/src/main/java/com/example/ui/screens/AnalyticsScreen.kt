package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FieldnoteUiState
import com.example.ui.theme.FieldnoteTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AnalyticsScreen(
    state: FieldnoteUiState,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    val totalTasks = state.tasks.size
    val completedTasks = state.tasks.count { it.status == "completed" }
    val completionRate = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks * 100).toInt() else 0

    val totalFocusMinutes = state.focusSessions.sumOf { it.durationMinutes }
    val maxHabitStreak = state.habits.maxOfOrNull { it.streak } ?: 0
    val totalHabitsCount = state.habits.size
    val completedHabitsToday = state.habits.count { it.completedDates.contains(todayStr) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = "Analytics & Rhythm",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Calm, non-judgmental visibility into your focus patterns",
                    fontSize = 13.sp,
                    color = colors.textMuted
                )
            }
        }

        // Hero KPI Grid (4 Metrics)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Completion Rate",
                        value = "$completionRate%",
                        subtitle = "$completedTasks of $totalTasks tasks",
                        accent = colors.accent,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Focus Time",
                        value = "${totalFocusMinutes}m",
                        subtitle = "${state.focusSessions.size} deep work blocks",
                        accent = colors.blue,
                        icon = Icons.Default.HourglassTop,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Active Habits",
                        value = "$completedHabitsToday / $totalHabitsCount",
                        subtitle = "Done today",
                        accent = colors.amber,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Top Streak",
                        value = "$maxHabitStreak days",
                        subtitle = "Unbroken consistency",
                        accent = Color(0xFFC7634C),
                        icon = Icons.Default.LocalFireDepartment,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly Distribution Visualizer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "WEEKLY TASK VOLUME",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val days = listOf("Mon" to 0.7f, "Tue" to 0.9f, "Wed" to 0.85f, "Thu" to 0.6f, "Fri" to 0.95f, "Sat" to 0.4f, "Sun" to 0.5f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEach { (day, factor) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 24.dp, height = (80 * factor).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (day == "Wed") colors.accent else colors.accent.copy(alpha = 0.35f))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(day, fontSize = 11.sp, color = colors.textMuted)
                            }
                        }
                    }
                }
            }
        }

        // Supportive AI Reflection Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.accent.copy(alpha = 0.12f))
                    .border(1.dp, colors.accent.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "✨ COPILOT PERSPECTIVE",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your output is highest on weekday mornings before 11:30 AM. Deep focus blocks remain consistent. Take rest when needed to sustain long-term cognitive stamina.",
                        fontSize = 12.5.sp,
                        color = colors.text,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title.uppercase(), fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = colors.textMuted, letterSpacing = 0.8.sp)
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.text)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = colors.textMuted)
        }
    }
}
