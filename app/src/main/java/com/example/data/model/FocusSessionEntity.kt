package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,
    val taskTitle: String,
    val durationMinutes: Int,
    val completed: Boolean = true,
    val mode: String = "pomodoro", // pomodoro, stopwatch, deep_work
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
