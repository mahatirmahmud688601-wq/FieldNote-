package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.calendar.CalendarSyncManager
import com.example.data.model.ChatMessageEntity
import com.example.data.model.TaskEntity
import java.util.UUID
import com.example.ui.FieldnoteUiState
import com.example.ui.theme.FieldnoteTheme

@Composable
fun CopilotDrawer(
    state: FieldnoteUiState,
    onSendMessage: (String) -> Unit,
    onExecuteAction: (ChatMessageEntity) -> Unit,
    onTriggerVoiceInput: (String?) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll when new messages arrive
    LaunchedEffect(state.chatMessages.size, state.isCopilotThinking) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.size - 1)
        }
    }

    val suggestions = listOf(
        "Plan my morning routine",
        "Add task: Review launch roadmap tomorrow at 2 PM",
        "Sync schedule with Google Calendar",
        "Summarize pending deadlines",
        "What tasks are overdue?",
        "Schedule 90-minute focus block"
    )

    AnimatedVisibility(
        visible = state.isCopilotOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = modifier.fillMaxSize()
    ) {
        // Scrim + Right-side slide-over drawer
        Box(modifier = Modifier.fillMaxSize()) {
            // Semi-transparent scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { onClose() }
            )

            // Right Slide-Over Panel
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.92f)
                    .align(Alignment.CenterEnd)
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Copilot Drawer Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Fieldnote Copilot",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.text
                            )
                            Text(
                                text = "Gemini 3.5 Flash & Offline Scheduling",
                                fontSize = 11.sp,
                                color = colors.textMuted
                            )
                        }
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.textMuted)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(colors.border)
                )

                // Message Stream
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.chatMessages, key = { it.id }) { message ->
                        ChatBubbleItem(
                            message = message,
                            onActionClick = { onExecuteAction(message) }
                        )
                    }

                    if (state.isCopilotThinking) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.surfaceMuted)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = colors.accent,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Copilot is analyzing your schedule...",
                                    fontSize = 12.sp,
                                    color = colors.textMuted
                                )
                            }
                        }
                    }

                    if (state.isVoiceListening) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.accentSoft)
                                    .border(1.dp, colors.accent, RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = colors.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Listening for voice command...",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.accent
                                    )
                                }
                            }
                        }
                    }
                }

                // Smart suggestions horizontal chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.forEach { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(colors.surfaceMuted)
                                .border(1.dp, colors.border, CircleShape)
                                .clickable { onSendMessage(suggestion) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 11.sp,
                                color = colors.text,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                // Input bar with Voice Command button & Send
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .border(1.dp, colors.border)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Hands-free Voice Command Mic Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (state.isVoiceListening) colors.accent else colors.surfaceMuted)
                                .clickable { onTriggerVoiceInput(null) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Command",
                                tint = if (state.isVoiceListening) Color.White else colors.text,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask Copilot or dictate schedule...", fontSize = 12.5.sp, color = colors.textMuted) },
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accent,
                                unfocusedBorderColor = colors.border,
                                focusedContainerColor = colors.bg,
                                unfocusedContainerColor = colors.bg,
                                focusedTextColor = colors.text,
                                unfocusedTextColor = colors.text
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copilot_text_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank()) colors.accent else colors.surfaceMuted)
                                .clickable {
                                    if (inputText.isNotBlank()) {
                                        onSendMessage(inputText)
                                        inputText = ""
                                    }
                                }
                                .testTag("copilot_send_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) Color.White else colors.textMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessageEntity,
    onActionClick: () -> Unit
) {
    val colors = FieldnoteTheme.colors
    val context = LocalContext.current
    val isUser = message.sender == "user"

    val parsedTask = remember(message.actionPayload) {
        message.actionPayload?.split("|")?.let { parts ->
            TaskEntity(
                id = UUID.randomUUID().toString(),
                title = parts.getOrNull(0) ?: "Scheduled Task",
                priority = parts.getOrNull(1) ?: "medium",
                dueDate = parts.getOrNull(2) ?: "",
                dueTime = parts.getOrNull(3) ?: "12:00",
                tags = "Copilot"
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(if (isUser) colors.accentSoft else colors.surfaceMuted)
                .border(
                    1.dp,
                    if (isUser) colors.accent.copy(alpha = 0.3f) else colors.border,
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .padding(14.dp)
                .fillMaxWidth(if (isUser) 0.85f else 0.95f)
        ) {
            Column {
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = if (isUser) colors.accent else colors.text
                )

                if (message.suggestedAction == "CREATE_TASK" && parsedTask != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onActionClick,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Task", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    CalendarSyncManager.openGoogleCalendarWeb(context, parsedTask)
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surface,
                                    contentColor = colors.text
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Google", fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                            }

                            Button(
                                onClick = {
                                    CalendarSyncManager.openOutlookCalendarWeb(context, parsedTask)
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.surface,
                                    contentColor = colors.text
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Outlook", fontSize = 10.5.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
