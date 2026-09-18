package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FieldnoteScreen
import com.example.ui.theme.FieldnoteTheme

@Composable
fun FieldnoteTopBar(
    currentScreen: FieldnoteScreen,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onToggleCopilot: () -> Unit,
    onToggleShowcase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bg)
            .border(1.dp, colors.border)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Geometric FN Logo Badge + Fieldnote Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onToggleShowcase() }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.accent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "FN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Fieldnote",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text,
                        letterSpacing = (-0.4).sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Geometric "SECURE" E2EE Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.border, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                    )
                    Text(
                        text = "SECURE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = colors.text.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Dark/Light Mode toggle pill button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.border, CircleShape)
                    .clickable { onToggleDarkTheme() }
                    .testTag("theme_toggle_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = colors.text.copy(alpha = 0.85f),
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Subtle Glowing "✨ Gemini Copilot" Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.accent.copy(alpha = 0.18f))
                    .border(1.dp, colors.accent, CircleShape)
                    .clickable { onToggleCopilot() }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("copilot_trigger_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini Copilot",
                        tint = colors.accent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "✨ Gemini Copilot",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accent,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FieldnoteBottomNavigation(
    currentScreen: FieldnoteScreen,
    onScreenSelected: (FieldnoteScreen) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Floating Pill Container with Matte Surface & Delicate 1px Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(colors.surface)
                .border(1.dp, colors.border, CircleShape)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTabItem(
                    label = "Home",
                    icon = Icons.Default.Dashboard,
                    selected = currentScreen == FieldnoteScreen.DASHBOARD,
                    onClick = { onScreenSelected(FieldnoteScreen.DASHBOARD) }
                )
                NavTabItem(
                    label = "Notes",
                    icon = Icons.Default.Notes,
                    selected = currentScreen == FieldnoteScreen.NOTES,
                    onClick = { onScreenSelected(FieldnoteScreen.NOTES) }
                )

                // Centered Glowing Floating Action Button (+)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(colors.accent)
                        .border(1.5.dp, colors.accent.copy(alpha = 0.7f), CircleShape)
                        .clickable { onFabClick() }
                        .testTag("quick_add_fab"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                NavTabItem(
                    label = "Tasks",
                    icon = Icons.Default.TaskAlt,
                    selected = currentScreen == FieldnoteScreen.TASKS,
                    onClick = { onScreenSelected(FieldnoteScreen.TASKS) }
                )
                NavTabItem(
                    label = "Calendar",
                    icon = Icons.Default.CalendarMonth,
                    selected = currentScreen == FieldnoteScreen.CALENDAR,
                    onClick = { onScreenSelected(FieldnoteScreen.CALENDAR) }
                )
                NavTabItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    selected = currentScreen == FieldnoteScreen.SETTINGS,
                    onClick = { onScreenSelected(FieldnoteScreen.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = FieldnoteTheme.colors
    val color = if (selected) colors.accent else colors.text.copy(alpha = 0.40f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.6.sp,
            color = color
        )
    }
}
