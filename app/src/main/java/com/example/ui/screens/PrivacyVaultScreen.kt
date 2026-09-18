package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.crypto.CryptoManager
import com.example.data.model.NoteEntity
import com.example.ui.FieldnoteUiState
import com.example.ui.theme.FieldnoteTheme

@Composable
fun PrivacyVaultScreen(
    state: FieldnoteUiState,
    onUnlock: (String) -> Boolean,
    onLock: () -> Unit,
    onOpenNote: (NoteEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = FieldnoteTheme.colors
    var passphraseInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val encryptedNotes = state.notes.filter { it.encrypted }

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
                    text = "Privacy Vault",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Hardware-backed AES-256-GCM zero-knowledge security",
                    fontSize = 13.sp,
                    color = colors.textMuted
                )
            }
        }

        if (!state.isVaultUnlocked) {
            // Locked Vault State
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.border, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(colors.accent.copy(alpha = 0.15f))
                                .border(1.dp, colors.accent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked Vault",
                                tint = colors.accent,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = "Vault is Encrypted",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.text
                        )

                        Text(
                            text = "Enter your master secret passphrase to decrypt hardware-guarded notes.",
                            fontSize = 12.5.sp,
                            color = colors.textMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        OutlinedTextField(
                            value = passphraseInput,
                            onValueChange = {
                                passphraseInput = it
                                errorMessage = null
                            },
                            label = { Text("Master Passphrase") },
                            placeholder = { Text("Default: fieldnote") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("vault_passphrase_input")
                        )

                        if (errorMessage != null) {
                            Text(errorMessage!!, color = colors.danger, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val success = onUnlock(passphraseInput)
                                if (!success) {
                                    errorMessage = "Invalid passphrase. Minimum 4 characters."
                                } else {
                                    passphraseInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("vault_unlock_button")
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unlock Vault", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } else {
            // Unlocked State: Show Encrypted Content
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.accent.copy(alpha = 0.15f))
                        .border(1.dp, colors.accent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Vault Decrypted", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colors.accent)
                                Text("${encryptedNotes.size} hardware-secured notes accessible", fontSize = 11.sp, color = colors.textMuted)
                            }
                        }

                        OutlinedButton(
                            onClick = onLock,
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lock", fontSize = 11.sp)
                        }
                    }
                }
            }

            if (encryptedNotes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No notes currently marked with encryption.",
                            fontSize = 13.sp,
                            color = colors.textMuted
                        )
                    }
                }
            } else {
                items(encryptedNotes) { note ->
                    val decryptedContent = if (note.encrypted) {
                        CryptoManager.decrypt(note.content)
                    } else {
                        note.content
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.border, RoundedCornerShape(20.dp))
                            .clickable { onOpenNote(note) }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(note.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = colors.text)
                                Icon(Icons.Default.Lock, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = decryptedContent,
                                fontSize = 12.5.sp,
                                color = colors.textMuted,
                                maxLines = 2,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
