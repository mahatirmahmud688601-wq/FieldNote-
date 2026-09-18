package com.example.data.repository

import android.content.Context
import com.example.data.crypto.CryptoManager
import com.example.data.db.FieldnoteDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class FieldnoteRepository(
    private val dao: FieldnoteDao,
    val localTaskRepository: TaskRepository = RoomTaskRepository(dao),
    val cloudTaskRepository: TaskRepository? = null
) {

    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    val allHabits: Flow<List<com.example.data.model.HabitEntity>> = dao.getAllHabits()
    val allFocusSessions: Flow<List<com.example.data.model.FocusSessionEntity>> = dao.getAllFocusSessions()

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingNotes = allNotes.first()
        val existingTasks = allTasks.first()
        val existingHabits = allHabits.first()

        if (existingHabits.isEmpty()) {
            val starterHabits = listOf(
                com.example.data.model.HabitEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Deep Work Session (90m)",
                    description = "Distraction-free focus on hardest task first thing in the morning",
                    frequency = "weekdays",
                    streak = 5,
                    bestStreak = 14,
                    completedDates = "",
                    targetDaysPerWeek = 5,
                    category = "Productivity",
                    color = "#8FA378"
                ),
                com.example.data.model.HabitEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Mindful Walk & Reflection",
                    description = "20-minute device-free stroll to clear cognitive fatigue",
                    frequency = "daily",
                    streak = 12,
                    bestStreak = 21,
                    completedDates = "",
                    targetDaysPerWeek = 7,
                    category = "Wellness",
                    color = "#7B9BB4"
                ),
                com.example.data.model.HabitEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Read 15 Pages of Non-fiction",
                    description = "Architecture, systems thinking, or philosophy books",
                    frequency = "daily",
                    streak = 8,
                    bestStreak = 19,
                    completedDates = "",
                    targetDaysPerWeek = 7,
                    category = "Learning",
                    color = "#D4A86A"
                )
            )
            starterHabits.forEach { dao.insertHabit(it) }
        }

        if (existingNotes.isEmpty() && existingTasks.isEmpty()) {
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = sdf.format(cal.time)

            // Yesterday for overdue demonstration
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)

            // Tomorrow
            cal.add(Calendar.DAY_OF_YEAR, 2)
            val tomorrowStr = sdf.format(cal.time)

            // In 3 days
            cal.add(Calendar.DAY_OF_YEAR, 2)
            val nextWeekStr = sdf.format(cal.time)

            val starterNotes = listOf(
                NoteEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Fieldnote Design Philosophy",
                    content = "Quiet craftsmanship inspired by Japanese Midori notebooks and Dieter Rams principles. High tactile clarity, generous negative space, and zero unnecessary visual friction.",
                    tags = "Philosophy, Design, Stationery",
                    pinned = true,
                    archived = false,
                    encrypted = false
                ),
                NoteEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Privacy & Zero-Knowledge Vault",
                    content = "All sensitive notes and personal schedules are guarded with hardware-accelerated AES-256-GCM. Decryption keys never leave this device.",
                    tags = "Security, Privacy, E2EE",
                    pinned = true,
                    archived = false,
                    encrypted = true
                ),
                NoteEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Tokyo Architecture & Teahouses",
                    content = "Notes on Nezu Museum garden paving, wood joinery at Toraya Akasaka, and paper textures for tactile journal covers.",
                    tags = "Inspiration, Travel",
                    pinned = false,
                    archived = false,
                    encrypted = false
                ),
                NoteEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Weekly Review Ritual",
                    content = "1. Clear pending tasks checklist.\n2. Review calendar timeline.\n3. Archive completed initiatives.\n4. Draft three key outcomes with Copilot.",
                    tags = "Productivity, Rituals",
                    pinned = false,
                    archived = false,
                    encrypted = false
                )
            )

            val starterTasks = listOf(
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Review Q3 product launch roadmap",
                    description = "Assess design system tokens, typography pairing, and export specifications.",
                    priority = "high",
                    status = "in_progress",
                    dueDate = todayStr,
                    dueTime = "11:00",
                    tags = "Launch, Design",
                    syncedWithCalendar = true
                ),
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Submit architecture proposal draft",
                    description = "Contractual delivery for engineering audit.",
                    priority = "high",
                    status = "todo",
                    dueDate = yesterdayStr,
                    dueTime = "17:00",
                    tags = "Urgent, Specs"
                ),
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Schedule team sync & calendar alignment",
                    description = "Coordinate upcoming sprint goals with engineering leads.",
                    priority = "medium",
                    status = "todo",
                    dueDate = todayStr,
                    dueTime = "15:30",
                    tags = "Meeting, Copilot"
                ),
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Order custom textured stationery samples",
                    description = "Off-white #FAFAF7 120gsm paper with sage green cloth binding.",
                    priority = "low",
                    status = "todo",
                    dueDate = tomorrowStr,
                    dueTime = "14:00",
                    tags = "Procurement"
                ),
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Setup End-to-End Encryption key backup",
                    description = "Verify local zero-knowledge passphrase in privacy settings.",
                    priority = "medium",
                    status = "completed",
                    dueDate = todayStr,
                    dueTime = "09:00",
                    tags = "Security"
                ),
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Quarterly investor presentation deck",
                    description = "Export high-resolution Figma slides and prototype links.",
                    priority = "medium",
                    status = "todo",
                    dueDate = nextWeekStr,
                    dueTime = "16:00",
                    tags = "Strategy"
                )
            )

            val initialChat = listOf(
                ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    sender = "copilot",
                    content = "Good day! I'm your Fieldnote AI Copilot. I can assist with hands-free daily scheduling, sync with your calendar, and organize your tasks with end-to-end privacy. Try asking: 'Plan my day' or tap the microphone for voice commands.",
                    timestamp = System.currentTimeMillis() - 60000
                )
            )

            dao.insertNotes(starterNotes)
            dao.insertTasks(starterTasks)
            initialChat.forEach { dao.insertChatMessage(it) }
        }
    }

    // --- Notes operations ---
    suspend fun saveNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        val processed = if (note.encrypted && !CryptoManager.isEncrypted(note.content)) {
            note.copy(content = CryptoManager.encrypt(note.content), updatedAt = System.currentTimeMillis())
        } else {
            note.copy(updatedAt = System.currentTimeMillis())
        }
        dao.insertNote(processed)
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.IO) {
        dao.deleteNoteById(id)
    }

    suspend fun togglePinNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        dao.updateNote(note.copy(pinned = !note.pinned, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleArchiveNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        dao.updateNote(note.copy(archived = !note.archived, updatedAt = System.currentTimeMillis()))
    }

    // --- Tasks operations ---
    suspend fun saveTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        val domainTask = com.example.data.model.Task.fromEntity(task)
        localTaskRepository.saveTask(domainTask)
        try {
            cloudTaskRepository?.saveTask(domainTask)
        } catch (_: Exception) {}
    }

    suspend fun deleteTask(id: String) = withContext(Dispatchers.IO) {
        localTaskRepository.deleteTask(id)
        try {
            cloudTaskRepository?.deleteTask(id)
        } catch (_: Exception) {}
    }

    suspend fun cycleTaskStatus(task: TaskEntity) = withContext(Dispatchers.IO) {
        val domainTask = com.example.data.model.Task.fromEntity(task)
        localTaskRepository.cycleTaskStatus(domainTask)
        try {
            cloudTaskRepository?.cycleTaskStatus(domainTask)
        } catch (_: Exception) {}
    }

    // --- Chat operations ---
    suspend fun saveChatMessage(message: ChatMessageEntity) = withContext(Dispatchers.IO) {
        dao.insertChatMessage(message)
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        dao.deleteAllChatMessages()
    }

    // --- Focus Sessions ---
    suspend fun saveFocusSession(session: com.example.data.model.FocusSessionEntity) = withContext(Dispatchers.IO) {
        dao.insertFocusSession(session)
    }

    // --- Habits ---
    suspend fun saveHabit(habit: com.example.data.model.HabitEntity) = withContext(Dispatchers.IO) {
        dao.insertHabit(habit)
    }

    suspend fun toggleHabitCompletion(habit: com.example.data.model.HabitEntity, dateStr: String) = withContext(Dispatchers.IO) {
        val completedList = habit.completedDates.split(",").filter { it.isNotBlank() }.toMutableSet()
        val isCurrentlyCompleted = completedList.contains(dateStr)
        val newStreak = if (isCurrentlyCompleted) {
            completedList.remove(dateStr)
            (habit.streak - 1).coerceAtLeast(0)
        } else {
            completedList.add(dateStr)
            habit.streak + 1
        }
        val best = maxOf(habit.bestStreak, newStreak)
        val updated = habit.copy(
            completedDates = completedList.joinToString(","),
            streak = newStreak,
            bestStreak = best
        )
        dao.updateHabit(updated)
    }

    suspend fun deleteHabit(id: String) = withContext(Dispatchers.IO) {
        dao.deleteHabitById(id)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        dao.deleteAllNotes()
        dao.deleteAllTasks()
        dao.deleteAllChatMessages()
        dao.deleteAllHabits()
        dao.deleteAllFocusSessions()
    }

    // --- JSON Export & Import ---
    suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
        val notes = allNotes.first()
        val tasks = allTasks.first()

        val root = JSONObject()
        root.put("version", "1.0")
        root.put("exportedAt", System.currentTimeMillis())
        root.put("encryption", "AES-256-GCM")

        val notesArray = JSONArray()
        notes.forEach { note ->
            val nObj = JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("content", note.content)
                put("tags", note.tags)
                put("pinned", note.pinned)
                put("archived", note.archived)
                put("encrypted", note.encrypted)
                put("createdAt", note.createdAt)
                put("updatedAt", note.updatedAt)
            }
            notesArray.put(nObj)
        }
        root.put("notes", notesArray)

        val tasksArray = JSONArray()
        tasks.forEach { task ->
            val tObj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("priority", task.priority)
                put("status", task.status)
                put("dueDate", task.dueDate)
                put("dueTime", task.dueTime)
                put("tags", task.tags)
                put("encrypted", task.encrypted)
                put("syncedWithCalendar", task.syncedWithCalendar)
                put("createdAt", task.createdAt)
                put("updatedAt", task.updatedAt)
            }
            tasksArray.put(tObj)
        }
        root.put("tasks", tasksArray)

        root.toString(2)
    }

    suspend fun importDataJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.has("notes")) {
                val notesArray = root.getJSONArray("notes")
                val notesList = mutableListOf<NoteEntity>()
                for (i in 0 until notesArray.length()) {
                    val obj = notesArray.getJSONObject(i)
                    notesList.add(
                        NoteEntity(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            title = obj.optString("title", "Untitled"),
                            content = obj.optString("content", ""),
                            tags = obj.optString("tags", ""),
                            pinned = obj.optBoolean("pinned", false),
                            archived = obj.optBoolean("archived", false),
                            encrypted = obj.optBoolean("encrypted", false),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertNotes(notesList)
            }

            if (root.has("tasks")) {
                val tasksArray = root.getJSONArray("tasks")
                val tasksList = mutableListOf<TaskEntity>()
                for (i in 0 until tasksArray.length()) {
                    val obj = tasksArray.getJSONObject(i)
                    tasksList.add(
                        TaskEntity(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            title = obj.optString("title", "Task"),
                            description = obj.optString("description", ""),
                            priority = obj.optString("priority", "medium"),
                            status = obj.optString("status", "todo"),
                            dueDate = obj.optString("dueDate", ""),
                            dueTime = obj.optString("dueTime", ""),
                            tags = obj.optString("tags", ""),
                            encrypted = obj.optBoolean("encrypted", false),
                            syncedWithCalendar = obj.optBoolean("syncedWithCalendar", false),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
                dao.insertTasks(tasksList)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
