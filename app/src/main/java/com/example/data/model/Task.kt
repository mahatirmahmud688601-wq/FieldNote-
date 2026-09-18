package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Domain and entity data model representing a Task.
 * Equipped with proper Room annotations for local SQLite storage,
 * and provides clean mapping between Room entities and Firestore documents.
 */
@Entity(tableName = "tasks")
data class Task(
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
    val status: String = if (isCompleted) "completed" else "todo",     // "todo", "in_progress", "completed"

    @ColumnInfo(name = "priority")
    val priority: String = "medium", // "low", "medium", "high"

    @ColumnInfo(name = "dueDate")
    val dueDate: String = "",        // YYYY-MM-DD

    @ColumnInfo(name = "dueTime")
    val dueTime: String = "",        // HH:mm

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

    /**
     * Converts domain Task to Room TaskEntity for local SQLite storage.
     */
    fun toEntity(): TaskEntity = TaskEntity(
        id = id,
        title = title,
        description = description,
        timestamp = timestamp,
        isCompleted = isCompleted || status == "completed",
        priority = priority,
        status = status,
        dueDate = dueDate,
        dueTime = dueTime,
        tags = tags,
        encrypted = encrypted,
        syncedWithCalendar = syncedWithCalendar,
        calendarEventId = calendarEventId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    /**
     * Converts domain Task to a Firestore-compatible Map representation.
     */
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "timestamp" to timestamp,
        "isCompleted" to isCompleted,
        "priority" to priority,
        "status" to status,
        "dueDate" to dueDate,
        "dueTime" to dueTime,
        "tags" to tags,
        "encrypted" to encrypted,
        "syncedWithCalendar" to syncedWithCalendar,
        "calendarEventId" to calendarEventId,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    companion object {
        /**
         * Creates domain Task from Room TaskEntity.
         */
        fun fromEntity(entity: TaskEntity): Task = Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            timestamp = entity.timestamp,
            isCompleted = entity.isCompleted || entity.status == "completed",
            priority = entity.priority,
            status = entity.status,
            dueDate = entity.dueDate,
            dueTime = entity.dueTime,
            tags = entity.tags,
            encrypted = entity.encrypted,
            syncedWithCalendar = entity.syncedWithCalendar,
            calendarEventId = entity.calendarEventId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )

        /**
         * Creates domain Task from a Firestore document snapshot map.
         */
        fun fromFirestoreMap(data: Map<String, Any?>, docId: String = ""): Task {
            val statusStr = (data["status"] as? String) ?: "todo"
            val isComp = (data["isCompleted"] as? Boolean) ?: (statusStr == "completed")
            val created = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            val time = (data["timestamp"] as? Number)?.toLong() ?: created
            return Task(
                id = (data["id"] as? String)?.takeIf { it.isNotBlank() } ?: docId,
                title = (data["title"] as? String) ?: "",
                description = (data["description"] as? String) ?: "",
                timestamp = time,
                isCompleted = isComp,
                status = statusStr,
                priority = (data["priority"] as? String) ?: "medium",
                dueDate = (data["dueDate"] as? String) ?: "",
                dueTime = (data["dueTime"] as? String) ?: "",
                tags = (data["tags"] as? String) ?: "",
                encrypted = (data["encrypted"] as? Boolean) ?: false,
                syncedWithCalendar = (data["syncedWithCalendar"] as? Boolean) ?: false,
                calendarEventId = (data["calendarEventId"] as? Number)?.toLong(),
                createdAt = created,
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}


/**
 * Convenience extension for TaskEntity to convert to domain Task.
 */
fun TaskEntity.toTask(): Task = Task.fromEntity(this)
