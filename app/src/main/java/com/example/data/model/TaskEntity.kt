package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Task entity class with proper Room annotations for local SQLite storage.
 * Includes fields for title, description, timestamp, and completion status.
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "title")
    val title: String = "",

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "status")
    val status: String = if (isCompleted) "completed" else "todo", // todo, in_progress, completed

    @ColumnInfo(name = "priority")
    val priority: String = "medium", // low, medium, high

    @ColumnInfo(name = "dueDate")
    val dueDate: String = "", // YYYY-MM-DD

    @ColumnInfo(name = "dueTime")
    val dueTime: String = "", // HH:mm

    @ColumnInfo(name = "tags")
    val tags: String = "",

    @ColumnInfo(name = "encrypted")
    val encrypted: Boolean = false,

    @ColumnInfo(name = "syncedWithCalendar")
    val syncedWithCalendar: Boolean = false,

    @ColumnInfo(name = "calendarEventId")
    val calendarEventId: Long? = null,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long = timestamp,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long = timestamp
) {
    /**
     * Convenience getter for completion status.
     */
    val isDone: Boolean
        get() = isCompleted || status == "completed"
}

