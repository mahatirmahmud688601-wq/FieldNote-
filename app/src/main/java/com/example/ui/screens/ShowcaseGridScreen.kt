package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FieldnoteScreen
import com.example.ui.FieldnoteUiState
import com.example.ui.components.FieldnoteBadge
import com.example.ui.theme.FieldnoteTheme

data class ShowcaseScreenItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val accentColor: Color,
    val features: List<String>,
    val targetScreen: FieldnoteScreen,
    val triggersCopilot: Boolean = false,
    val triggersEditor: Boolean = false,
    val triggersTaskSheet: Boolean = false
)

@Composable
fun ShowcaseGridScreen(
    state: FieldnoteUiState,
    onNavigate: (FieldnoteScreen) -> Unit,
    onOpenCopilot: () -> Unit,
    onOpenNoteEditor: () -> Unit,
    onOpenTaskSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors

    val screensList = listOf(
        ShowcaseScreenItem(
            id = 1,
            title = "1. Main Dashboard",
            subtitle = "Overview & Daily Horizon",
            category = "Core View",
            icon = Icons.Default.Dashboard,
            accentColor = colors.accent,
            features = listOf("Stat cards (Notes, Tasks, Completed, Overdue)", "Today's dynamic agenda checklist", "Recent notes preview stream", "E2EE security status"),
            targetScreen = FieldnoteScreen.DASHBOARD
        ),
        ShowcaseScreenItem(
            id = 2,
            title = "2. Notes Gallery",
            subtitle = "Multi-tag Idea Vault",
            category = "Knowledge",
            icon = Icons.AutoMirrored.Filled.Notes,
            accentColor = colors.blue,
            features = listOf("Tag pill filter ribbon", "Real-time search across title/content", "Pin to top & archive capabilities", "AES-256 encrypted note locks"),
            targetScreen = FieldnoteScreen.NOTES
        ),
        ShowcaseScreenItem(
            id = 3,
            title = "3. Distraction-Free Note Editor",
            subtitle = "Japanese Stationery Canvas",
            category = "Writing",
            icon = Icons.Default.EditNote,
            accentColor = colors.amber,
            features = listOf("Zero-noise typography canvas", "Instant AES-256 encryption toggle", "Auto-save status indicator", "Tag editor & note pinning"),
            targetScreen = FieldnoteScreen.NOTES,
            triggersEditor = true
        ),
        ShowcaseScreenItem(
            id = 4,
            title = "4. Tasks Checklist",
            subtitle = "Prioritized Action Engine",
            category = "Productivity",
            icon = Icons.Default.TaskAlt,
            accentColor = colors.accent,
            features = listOf("Circular 3-state toggle (Todo/Progress/Done)", "Priority tags (High, Medium, Low)", "Overdue warnings & sorting", "Calendar sync shortcut"),
            targetScreen = FieldnoteScreen.TASKS
        ),
        ShowcaseScreenItem(
            id = 5,
            title = "5. Task Creation Modal",
            subtitle = "Fast Structured Input",
            category = "Capture",
            icon = Icons.Default.PlaylistAdd,
            accentColor = colors.danger,
            features = listOf("Priority level segmented pills", "Due date & 24h/12h time input", "Android Calendar auto-sync toggle", "Tags & agenda notes"),
            targetScreen = FieldnoteScreen.TASKS,
            triggersTaskSheet = true
        ),
        ShowcaseScreenItem(
            id = 6,
            title = "6. Calendar Timeline",
            subtitle = "Chronological Horizon",
            category = "Schedule",
            icon = Icons.Default.CalendarMonth,
            accentColor = colors.blue,
            features = listOf("Overdue deadlines alert banner", "Grouped chronological timeline", "Open in native device Calendar app", "Direct completion checkmarks"),
            targetScreen = FieldnoteScreen.CALENDAR
        ),
        ShowcaseScreenItem(
            id = 7,
            title = "7. Settings & Zero-Knowledge Vault",
            subtitle = "Privacy & Encryption Suite",
            category = "Security",
            icon = Icons.Default.Settings,
            accentColor = colors.textMuted,
            features = listOf("Light (#FAFAF7) / Dark mode toggle", "Master passphrase rotation", "Encrypted JSON data export & import", "Test real-time notification alerts"),
            targetScreen = FieldnoteScreen.SETTINGS
        ),
        ShowcaseScreenItem(
            id = 8,
            title = "8. Gemini AI Copilot",
            subtitle = "Hands-Free Voice & Assistant",
            category = "Intelligence",
            icon = Icons.Default.AutoAwesome,
            accentColor = colors.accent,
            features = listOf("Gemini 3.5 Flash natural language scheduling", "Hands-free voice command planning", "Smart suggestion chips", "1-tap 'Add to Checklist' action"),
            targetScreen = FieldnoteScreen.DASHBOARD,
            triggersCopilot = true
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Showcase Presentation Header
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(colors.accent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DESIGN SYSTEM PRESENTATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fieldnote Architecture",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "High-fidelity UI/UX showcase of all 8 application screens, design tokens, and encryption protocols in an isometric portfolio layout.",
                    fontSize = 12.5.sp,
                    color = colors.textMuted,
                    lineHeight = 18.sp
                )
            }
        }

        // 16:9 Publication-Ready Feature Graphic Showcase Banner
        item {
            FeatureGraphicShowcaseBanner()
        }

        // Design Tokens & Color Swatches Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Color Palette & Material Tokens",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.text
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaletteSwatch(label = "Graphite", hex = "#121310", color = Color(0xFF121310), textColor = Color.White)
                        PaletteSwatch(label = "Surface", hex = "#1A1B16", color = Color(0xFF1A1B16), textColor = Color.White)
                        PaletteSwatch(label = "Border", hex = "#2E3027", color = Color(0xFF2E3027), textColor = Color.White)
                        PaletteSwatch(label = "Sage", hex = "#8FA378", color = Color(0xFF8FA378), textColor = Color(0xFF121310))
                        PaletteSwatch(label = "Paper", hex = "#FAFAF7", color = Color(0xFFFAFAF7), textColor = Color(0xFF121310))
                        PaletteSwatch(label = "Ochre", hex = "#D4A86A", color = Color(0xFFD4A86A), textColor = Color(0xFF121310))
                        PaletteSwatch(label = "Slate", hex = "#7B9BB4", color = Color(0xFF7B9BB4), textColor = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SecurityBadgeItem(title = "AES-256-GCM", desc = "Zero-Knowledge")
                        SecurityBadgeItem(title = "Room SQLite", desc = "100% Offline")
                        SecurityBadgeItem(title = "Google Gemini", desc = "Smart Copilot")
                        SecurityBadgeItem(title = "Device Sync", desc = "Android Calendar")
                    }
                }
            }
        }

        item {
            Text(
                text = "Interactive Screen Portfolio (8 Screens)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text
            )
        }

        // 8 Interactive Screen Cards
        items(screensList, key = { it.id }) { item ->
            ShowcaseScreenCard(
                item = item,
                onOpen = {
                    when {
                        item.triggersCopilot -> {
                            onNavigate(item.targetScreen)
                            onOpenCopilot()
                        }
                        item.triggersEditor -> {
                            onNavigate(item.targetScreen)
                            onOpenNoteEditor()
                        }
                        item.triggersTaskSheet -> {
                            onNavigate(item.targetScreen)
                            onOpenTaskSheet()
                        }
                        else -> {
                            onNavigate(item.targetScreen)
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PaletteSwatch(
    label: String,
    hex: String,
    color: Color,
    textColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(1.dp, Color(0xFFE4E2DA), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(label.take(1), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
        Text(hex, fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun SecurityBadgeItem(title: String, desc: String) {
    val colors = FieldnoteTheme.colors
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.accent)
        Text(text = desc, fontSize = 9.5.sp, color = colors.textMuted)
    }
}

@Composable
fun ShowcaseScreenCard(
    item: ShowcaseScreenItem,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(item.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = item.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )
                        Text(
                            text = item.subtitle,
                            fontSize = 11.5.sp,
                            color = colors.textMuted
                        )
                    }
                }

                FieldnoteBadge(
                    text = item.category,
                    backgroundColor = item.accentColor.copy(alpha = 0.12f),
                    contentColor = item.accentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bullet features
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surfaceMuted)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item.features.forEach { feat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "•", fontSize = 12.sp, color = item.accentColor, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = feat, fontSize = 11.5.sp, color = colors.text, lineHeight = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onOpen,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Text("Launch Screen", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

// 16:9 Publication-Ready Google Play & App Store Feature Graphic Banner
@Composable
fun FeatureGraphicShowcaseBanner(
    modifier: Modifier = Modifier
) {
    val sageGreen = Color(0xFF8FA378)
    val deepGraphite = Color(0xFF121310)
    val slateCharcoal = Color(0xFF1E1F1B)
    val cardSurface = Color(0xFF1A1B16)
    val delicateBorder = Color(0xFF2E3027)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .shadow(24.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF282A22),
                        slateCharcoal,
                        deepGraphite
                    ),
                    radius = 850f
                )
            )
            .border(1.dp, delicateBorder, RoundedCornerShape(24.dp))
    ) {
        // Soft Studio Background with Subtle Abstract 3D Clay Spheres & Ambient Gradients
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Ambient sage green studio glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(sageGreen.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(size.width * 0.82f, size.height * 0.22f),
                    radius = size.width * 0.42f
                ),
                radius = size.width * 0.42f,
                center = Offset(size.width * 0.82f, size.height * 0.22f)
            )
            // Slate charcoal lower ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2E3227).copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width * 0.18f, size.height * 0.85f),
                    radius = size.width * 0.35f
                ),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.18f, size.height * 0.85f)
            )
            // Left 3D clay sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF383B30), Color(0xFF181A14)),
                    center = Offset(size.width * 0.07f, size.height * 0.65f),
                    radius = 32.dp.toPx()
                ),
                radius = 28.dp.toPx(),
                center = Offset(size.width * 0.07f, size.height * 0.65f)
            )
            // Right 3D clay sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(sageGreen.copy(alpha = 0.5f), Color(0xFF23251D)),
                    center = Offset(size.width * 0.93f, size.height * 0.72f),
                    radius = 26.dp.toPx()
                ),
                radius = 24.dp.toPx(),
                center = Offset(size.width * 0.93f, size.height * 0.72f)
            )
            // Small floating clay sphere
            drawCircle(
                color = sageGreen.copy(alpha = 0.65f),
                radius = 4.5.dp.toPx(),
                center = Offset(size.width * 0.22f, size.height * 0.18f)
            )
        }

        // Top Header: Subtle floating headline
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "FIELDNOTE",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.4.sp,
                    color = sageGreen.copy(alpha = 0.9f)
                )
                Text(
                    text = "Simplify Your Daily Workflow",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    color = Color(0xFFFAFAF7).copy(alpha = 0.95f)
                )
            }
        }

        // Three Floating Angled Flagship Smartphones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            // LEFT PHONE: Distraction-free Notes View
            AngledPhoneMockup(
                modifier = Modifier
                    .weight(0.92f)
                    .height(134.dp)
                    .offset(y = 8.dp)
                    .rotate(-5f),
                borderColor = delicateBorder
            ) {
                MockupNotesScreen(cardSurface, delicateBorder, sageGreen)
            }

            Spacer(modifier = Modifier.width(5.dp))

            // CENTER PHONE (Hero): Bento Grid Dashboard
            AngledPhoneMockup(
                modifier = Modifier
                    .weight(1.1f)
                    .height(148.dp)
                    .offset(y = (-2).dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp)),
                borderColor = sageGreen.copy(alpha = 0.85f),
                isHero = true
            ) {
                MockupDashboardHeroScreen(cardSurface, delicateBorder, sageGreen)
            }

            Spacer(modifier = Modifier.width(5.dp))

            // RIGHT PHONE: Interactive Timeline Schedule & Task Creation
            AngledPhoneMockup(
                modifier = Modifier
                    .weight(0.92f)
                    .height(134.dp)
                    .offset(y = 8.dp)
                    .rotate(5f),
                borderColor = delicateBorder
            ) {
                MockupTimelineScreen(cardSurface, delicateBorder, sageGreen)
            }
        }

        // Top-left Store Badge Pill
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF10110E).copy(alpha = 0.85f))
                .border(0.8.dp, delicateBorder, CircleShape)
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(
                text = "16:9 PROMOTIONAL SHOWCASE",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = sageGreen
            )
        }
    }
}

@Composable
private fun AngledPhoneMockup(
    modifier: Modifier = Modifier,
    borderColor: Color,
    isHero: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0C0D0A))
            .border(if (isHero) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(2.5.dp)
    ) {
        // Internal Screen Container with Razor-thin Bezel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFF171815))
        ) {
            content()

            // Punch hole camera at top center
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 3.dp)
                    .size(width = 18.dp, height = 4.5.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            )
        }
    }
}

// Left Screen Mockup: Notes View with clean card grids, tags, and pinned items
@Composable
private fun MockupNotesScreen(
    cardSurface: Color,
    border: Color,
    accent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 9.dp)
    ) {
        // Top search bar mini pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.dp)
                .clip(CircleShape)
                .background(cardSurface)
                .border(0.5.dp, border, CircleShape)
                .padding(horizontal = 5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Search notes...", fontSize = 6.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Tag Filter Ribbon
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accent)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text("All", fontSize = 5.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(cardSurface)
                    .border(0.5.dp, border, CircleShape)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text("Design", fontSize = 5.5.sp, color = Color.White)
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(cardSurface)
                    .border(0.5.dp, border, CircleShape)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text("Strategy", fontSize = 5.5.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Pinned Note Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(cardSurface)
                .border(0.8.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Product Architecture", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(7.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Zero-knowledge AES-256 local encryption with clean SQLite storage.",
                    fontSize = 5.5.sp,
                    color = Color.LightGray,
                    lineHeight = 7.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Second Note Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(cardSurface)
                .border(0.5.dp, border, RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            Column {
                Text("Brand Guidelines", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Warm sage #8FA378 accents with Scandinavian minimalism.", fontSize = 5.5.sp, color = Color.Gray, lineHeight = 7.sp)
            }
        }
    }
}

// Center Screen Mockup (Hero): Bento Grid Dashboard in refined dark mode (#171815)
@Composable
private fun MockupDashboardHeroScreen(
    cardSurface: Color,
    border: Color,
    accent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 7.dp, vertical = 8.dp)
    ) {
        // Top Bar: Typography header with glowing "✨ Gemini Copilot" pill badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FIELDNOTE",
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = Color.White
            )

            // Glowing "✨ Gemini Copilot" Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.2f))
                    .border(0.8.dp, accent, CircleShape)
                    .padding(horizontal = 4.5.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(6.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "✨ Copilot",
                        fontSize = 5.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Hero Bento Card: "Good morning" with 75% Circular Progress Ring
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(cardSurface)
                .border(0.8.dp, border, RoundedCornerShape(10.dp))
                .padding(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("TODAY • WEDNESDAY", fontSize = 5.5.sp, fontWeight = FontWeight.Bold, color = accent)
                    Text("Good morning.", fontSize = 8.sp, fontWeight = FontWeight.Light, color = Color.White)
                    Text("3 key tasks scheduled.", fontSize = 6.sp, color = Color.LightGray)

                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accent)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text("Plan My Day", fontSize = 5.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // 75% Circular Progress Ring
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 3.dp.toPx()
                        drawCircle(color = border, style = Stroke(width = stroke))
                        drawArc(
                            color = accent,
                            startAngle = -90f,
                            sweepAngle = 270f, // 75%
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                    Text("75%", fontSize = 6.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Priority Tasks Checklist
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            // Task item 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(cardSurface)
                    .border(0.5.dp, border, RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(6.dp))
                }
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(Color(0xFFC7634C)))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Design review presentation", fontSize = 5.5.sp, color = Color.LightGray)
            }

            // Task item 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(cardSurface)
                    .border(0.5.dp, border, RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .border(0.8.dp, accent, CircleShape)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Box(modifier = Modifier.size(3.5.dp).clip(CircleShape).background(Color(0xFFD4A86A)))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Sync Google Calendar tasks", fontSize = 5.5.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Docked Floating Navigation Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(0xFF1E1F1B))
                .border(0.6.dp, border, CircleShape)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Dashboard, contentDescription = null, tint = accent, modifier = Modifier.size(8.dp))
                Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(8.dp))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(accent))
                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(8.dp))
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(8.dp))
            }
        }
    }
}

// Right Screen Mockup: Interactive Timeline Schedule & Task Creation Bottom-sheet
@Composable
private fun MockupTimelineScreen(
    cardSurface: Color,
    border: Color,
    accent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TIMELINE", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Sep 02", fontSize = 6.sp, color = accent)
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Chronological events
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TimelineEventItem("09:00 AM", "Product Architecture Sync", accent, cardSurface, border)
            TimelineEventItem("11:30 AM", "Mobile UI/UX Critique", Color(0xFFD4A86A), cardSurface, border)
            TimelineEventItem("02:00 PM", "Sprint Planning & Horizon", Color(0xFF7B9BB4), cardSurface, border)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Task Creation Bottom-Sheet Modal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .background(cardSurface)
                .border(0.8.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                .padding(6.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("+ New Task", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(accent), contentAlignment = Alignment.Center) {
                        Text("✓", fontSize = 6.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Box(modifier = Modifier.clip(CircleShape).background(Color(0xFFC7634C).copy(alpha = 0.25f)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                        Text("HIGH", fontSize = 5.sp, color = Color(0xFFC7634C), fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.clip(CircleShape).background(accent.copy(alpha = 0.2f)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                        Text("10:00 AM", fontSize = 5.sp, color = accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEventItem(
    time: String,
    title: String,
    dotColor: Color,
    surface: Color,
    border: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(surface)
            .border(0.5.dp, border, RoundedCornerShape(6.dp))
            .padding(horizontal = 5.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(dotColor))
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(time, fontSize = 5.sp, color = dotColor, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 5.5.sp, color = Color.White, maxLines = 1)
        }
    }
}
