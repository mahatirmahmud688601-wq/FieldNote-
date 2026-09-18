package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.crypto.CryptoManager
import com.example.data.model.NoteEntity
import com.example.ui.components.ConfirmDialog
import com.example.ui.theme.FieldnoteTheme

@Composable
fun NoteEditorScreen(
    initialNote: NoteEntity?,
    onSave: (NoteEntity) -> Unit,
    onDelete: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val isNew = initialNote == null || initialNote.title.isBlank() && initialNote.content.isBlank()

    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var content by remember {
        val initialContent = initialNote?.content ?: ""
        val decrypted = if (initialNote?.encrypted == true) {
            CryptoManager.decrypt(initialContent)
        } else {
            initialContent
        }
        mutableStateOf(decrypted)
    }
    var tagsInput by remember { mutableStateOf(initialNote?.tags ?: "") }
    var isPinned by remember { mutableStateOf(initialNote?.pinned ?: false) }
    var isArchived by remember { mutableStateOf(initialNote?.archived ?: false) }
    var isEncrypted by remember { mutableStateOf(initialNote?.encrypted ?: false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun buildNote(): NoteEntity {
        return (initialNote ?: NoteEntity(
            id = java.util.UUID.randomUUID().toString(),
            title = title,
            content = content
        )).copy(
            title = title.trim(),
            content = content.trim(),
            tags = tagsInput.trim(),
            pinned = isPinned,
            archived = isArchived,
            encrypted = isEncrypted,
            updatedAt = System.currentTimeMillis()
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (title.isNotBlank() || content.isNotBlank()) {
                    onSave(buildNote())
                } else {
                    onClose()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.text,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = if (isEncrypted) "E2EE Guarded" else "Stationery Note",
                fontSize = 12.sp,
                color = if (isEncrypted) colors.amber else colors.textMuted,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Encryption Toggle
                IconButton(onClick = { isEncrypted = !isEncrypted }) {
                    Icon(
                        imageVector = if (isEncrypted) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Encrypt",
                        tint = if (isEncrypted) colors.amber else colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Pin Toggle
                IconButton(onClick = { isPinned = !isPinned }) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pin",
                        tint = if (isPinned) colors.accent else colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Archive Toggle
                IconButton(onClick = { isArchived = !isArchived }) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Archive",
                        tint = if (isArchived) colors.accent else colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (!isNew && initialNote != null) {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = colors.danger,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Done / Save
                Button(
                    onClick = {
                        onSave(buildNote())
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .testTag("note_editor_save_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", fontSize = 12.sp)
                }
            }
        }

        // Editor Canvas
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Note Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        text = "Untitled Note",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textMuted.copy(alpha = 0.6f)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_editor_title_input")
            )

            // Tags Input
            OutlinedTextField(
                value = tagsInput,
                onValueChange = { tagsInput = it },
                placeholder = {
                    Text(
                        text = "Add tags (e.g. Design, Philosophy, Specs)",
                        fontSize = 12.5.sp,
                        color = colors.textMuted
                    )
                },
                textStyle = TextStyle(
                    fontSize = 12.5.sp,
                    color = colors.accent,
                    fontWeight = FontWeight.Medium
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Subtle divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.border.copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Multiline Writing Content Canvas
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = {
                    Text(
                        text = "Start writing your thoughts...\n\nDistraction-free stationery canvas with zero visual noise.",
                        fontSize = 14.5.sp,
                        lineHeight = 22.sp,
                        color = colors.textMuted.copy(alpha = 0.6f)
                    )
                },
                textStyle = TextStyle(
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                    color = colors.text
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .testTag("note_editor_content_input")
            )
        }
    }

    ConfirmDialog(
        open = showDeleteConfirm,
        title = "Delete note?",
        message = "Are you sure you want to delete this note permanently?",
        confirmLabel = "Delete",
        isDanger = true,
        onConfirm = {
            initialNote?.id?.let { onDelete(it) }
            showDeleteConfirm = false
        },
        onCancel = { showDeleteConfirm = false }
    )
}
