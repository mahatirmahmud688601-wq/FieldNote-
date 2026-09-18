package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FieldnoteUiState
import com.example.ui.theme.FieldnoteTheme
import kotlinx.coroutines.delay

@Composable
fun FocusModeScreen(
    state: FieldnoteUiState,
    onStartFocus: (Int) -> Unit,
    onCompleteFocus: (String, Int, String) -> Unit,
    onCancelFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    var selectedMinutes by remember { mutableIntStateOf(25) }
    var secondsRemaining by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var currentTaskName by remember { mutableStateOf("Deep Architecture Work") }

    // Countdown effect
    LaunchedEffect(isRunning) {
        while (isRunning && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining -= 1
        }
        if (isRunning && secondsRemaining == 0) {
            isRunning = false
            onCompleteFocus(currentTaskName, selectedMinutes, "pomodoro")
            secondsRemaining = selectedMinutes * 60
        }
    }

    val totalSeconds = (selectedMinutes * 60).coerceAtLeast(1)
    val progress = (secondsRemaining.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val displayMin = secondsRemaining / 60
    val displaySec = secondsRemaining % 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Focus Mode",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Distraction-free environment for deep concentration",
                    fontSize = 13.sp,
                    color = colors.textMuted
                )
            }
        }

        // Duration Presets Pill Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf(15, 25, 45, 60, 90).forEach { mins ->
                    val isSelected = selectedMinutes == mins
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) colors.accent else colors.surface)
                            .border(1.dp, if (isSelected) colors.accent else colors.border, CircleShape)
                            .clickable {
                                if (!isRunning) {
                                    selectedMinutes = mins
                                    secondsRemaining = mins * 60
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "${mins}m",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else colors.textMuted
                        )
                    }
                }
            }
        }

        // Circular Minimalist Pomodoro Ring
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 10.dp.toPx()
                    drawCircle(
                        color = colors.surface,
                        style = Stroke(width = stroke)
                    )
                    drawArc(
                        color = colors.accent,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", displayMin, displaySec),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace,
                        color = colors.text,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "Deep Focus Active" else "Ready to Focus",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.accent
                    )
                }
            }
        }

        // Controls
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isRunning) {
                    Button(
                        onClick = {
                            isRunning = true
                            onStartFocus(selectedMinutes)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                        shape = CircleShape,
                        modifier = Modifier
                            .height(48.dp)
                            .padding(horizontal = 16.dp)
                            .testTag("start_focus_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Begin Session", fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                } else {
                    OutlinedButton(
                        onClick = { isRunning = false },
                        shape = CircleShape,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pause")
                    }

                    Button(
                        onClick = {
                            val elapsedMinutes = (totalSeconds - secondsRemaining) / 60
                            if (elapsedMinutes >= 1) {
                                onCompleteFocus(currentTaskName, elapsedMinutes, "pomodoro")
                            }
                            isRunning = false
                            secondsRemaining = selectedMinutes * 60
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.danger),
                        shape = CircleShape,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("End Session", color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CircleShape)
                        .clickable {
                            isRunning = false
                            secondsRemaining = selectedMinutes * 60
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = colors.textMuted, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Recent Sessions Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "RECENT FOCUS SESSIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (state.focusSessions.isEmpty()) {
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
                        text = "No focus sessions recorded yet today.",
                        fontSize = 12.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            items(state.focusSessions.take(5)) { session ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colors.accent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(session.taskTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.text)
                                Text("${session.mode.replace("_", " ").uppercase()} • Completed", fontSize = 10.sp, color = colors.textMuted)
                            }
                        }
                        Text("+${session.durationMinutes}m", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.accent)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
