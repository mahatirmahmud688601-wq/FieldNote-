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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.crypto.CryptoManager
import com.example.data.model.NoteEntity
import com.example.ui.FieldnoteUiState
import com.example.ui.components.ConfirmDialog
import com.example.ui.components.FieldnoteBadge
import com.example.ui.components.FieldnoteTextField
import com.example.ui.theme.FieldnoteTheme

@Composable
fun NotesGalleryScreen(
    state: FieldnoteUiState,
    onOpenNote: (NoteEntity?) -> Unit,
    onTogglePin: (NoteEntity) -> Unit,
    onToggleArchive: (NoteEntity) -> Unit,
    onDeleteNote: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onTagFilterChange: (String) -> Unit,
    onSortChange: (String) -> Unit,
    onToggleShowArchived: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    var noteToDeleteId by remember { mutableStateOf<String?>(null) }

    // Collect all distinct tags
    val allTags = remember(state.notes) {
        val tags = mutableSetOf<String>()
        state.notes.forEach { note ->
            note.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tags.add(it) }
        }
        tags.toList()
    }

    // Filter notes
    val filteredNotes = remember(
        state.notes,
        state.searchQuery,
        state.selectedTagFilter,
        state.notesSortBy,
        state.showArchivedNotes
    ) {
        var list = state.notes.filter { if (state.showArchivedNotes) it.archived else !it.archived }
        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.content.lowercase().contains(q) ||
                it.tags.lowercase().contains(q)
            }
        }
        if (state.selectedTagFilter != "all") {
            list = list.filter {
                it.tags.split(",").map { t -> t.trim().lowercase() }.contains(state.selectedTagFilter.lowercase())
            }
        }
        list = when (state.notesSortBy) {
            "newest" -> list.sortedByDescending { it.createdAt }
            "oldest" -> list.sortedBy { it.createdAt }
            else -> list.sortedByDescending { it.updatedAt }
        }
        list.sortedByDescending { it.pinned }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title and + New Note
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (state.showArchivedNotes) "Archived Notes" else "Notes Gallery",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                letterSpacing = (-0.4).sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onToggleShowArchived,
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (state.showArchivedNotes) colors.accentSoft else colors.surface,
                        contentColor = if (state.showArchivedNotes) colors.accent else colors.textMuted
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
                ) {
                    Icon(
                        imageVector = if (state.showArchivedNotes) Icons.Default.Unarchive else Icons.Default.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (state.showArchivedNotes) "Exit Archive" else "Archive", fontSize = 11.5.sp)
                }

                Button(
                    onClick = { onOpenNote(null) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("gallery_new_note_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Note", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        FieldnoteTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Search notes by title, tag, or content...",
            leadingIcon = Icons.Default.Search,
            testTag = "notes_search_input"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tag filter chips horizontal row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagFilterChip(
                label = "All",
                selected = state.selectedTagFilter == "all",
                onClick = { onTagFilterChange("all") }
            )
            allTags.forEach { tag ->
                TagFilterChip(
                    label = tag,
                    selected = state.selectedTagFilter == tag,
                    onClick = { onTagFilterChange(tag) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (state.showArchivedNotes) "No archived notes found." else "No notes found.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.text
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Create a new note or adjust your search filter.",
                        fontSize = 12.5.sp,
                        color = colors.textMuted
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredNotes, key = { it.id }) { note ->
                    NoteGalleryCard(
                        note = note,
                        onOpen = { onOpenNote(note) },
                        onTogglePin = { onTogglePin(note) },
                        onToggleArchive = { onToggleArchive(note) },
                        onDelete = { noteToDeleteId = note.id }
                    )
                }
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    ConfirmDialog(
        open = noteToDeleteId != null,
        title = "Delete note?",
        message = "This note will be permanently removed from your local vault.",
        confirmLabel = "Delete",
        isDanger = true,
        onConfirm = {
            noteToDeleteId?.let { onDeleteNote(it) }
            noteToDeleteId = null
        },
        onCancel = { noteToDeleteId = null }
    )
}

@Composable
private fun TagFilterChip(
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
fun NoteGalleryCard(
    note: NoteEntity,
    onOpen: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    val decryptedContent = if (note.encrypted) CryptoManager.decrypt(note.content) else note.content
    val tagsList = note.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(colors.surface)
            .border(1.dp, if (note.pinned) colors.accent else colors.border, RoundedCornerShape(24.dp))
            .clickable { onOpen() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title.ifBlank { "Untitled Note" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (note.pinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = colors.accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
                if (note.encrypted) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = colors.amber,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = decryptedContent.ifBlank { "No content yet." },
                fontSize = 12.sp,
                color = colors.textMuted,
                lineHeight = 16.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(64.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (tagsList.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tagsList.take(2).forEach { tag ->
                        FieldnoteBadge(text = tag)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onTogglePin, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (note.pinned) Icons.Default.PushPin else Icons.Default.Pin,
                        contentDescription = "Pin",
                        tint = if (note.pinned) colors.accent else colors.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(onClick = onToggleArchive, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (note.archived) Icons.Default.Unarchive else Icons.Default.Archive,
                        contentDescription = "Archive",
                        tint = colors.textMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = colors.danger,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
