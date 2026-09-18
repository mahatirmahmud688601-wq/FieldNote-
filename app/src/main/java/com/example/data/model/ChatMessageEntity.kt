package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sender: String, // "user" or "copilot"
    val content: String,
    val suggestedAction: String? = null,
    val actionPayload: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
