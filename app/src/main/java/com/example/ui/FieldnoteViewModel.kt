package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiCopilotService
import com.example.data.calendar.CalendarSyncManager
import com.example.data.crypto.CryptoManager
import com.example.data.db.FieldnoteDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import com.example.data.notification.ReminderNotificationManager
import com.example.data.repository.FieldnoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

enum class FieldnoteScreen(val title: String) {
    ONBOARDING("Welcome"),
    DASHBOARD("Dashboard"),
    NOTES("Notes"),
    TASKS("Tasks"),
    CALENDAR("Calendar"),
    FOCUS("Focus Mode"),
    HABITS("Habits & Routines"),
    ANALYTICS("Analytics"),
    VAULT("Privacy Vault"),
    SETTINGS("Settings"),
    SHOWCASE("Showcase Grid")
}

data class FieldnoteUiState(
    val notes: List<NoteEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val chatMessages: List<ChatMessageEntity> = emptyList(),
    val habits: List<com.example.data.model.HabitEntity> = emptyList(),
    val focusSessions: List<com.example.data.model.FocusSessionEntity> = emptyList(),
    val currentScreen: FieldnoteScreen = FieldnoteScreen.DASHBOARD,
    val isCopilotOpen: Boolean = false,
    val isDarkTheme: Boolean = true,
    val defaultPriority: String = "medium",
    val notificationsEnabled: Boolean = true,
    val isCopilotThinking: Boolean = false,
    val isVoiceListening: Boolean = false,
    val activeEditingNote: NoteEntity? = null,
    val isNoteEditorOpen: Boolean = false,
    val activeEditingTask: TaskEntity? = null,
    val isTaskSheetOpen: Boolean = false,
    val isNlConfirmOpen: Boolean = false,
    val parsedNlTask: com.example.data.nlp.ParsedTaskResult? = null,
    val toastMessage: String? = null,
    val searchQuery: String = "",
    val selectedTagFilter: String = "all",
    val taskStatusFilter: String = "all",
    val taskPriorityFilter: String = "all",
    val tasksSortBy: String = "due",
    val notesSortBy: String = "updated",
    val showArchivedNotes: Boolean = false,
    val isVaultUnlocked: Boolean = false,
    val currentFocusMinutes: Int = 25,
    val isFocusTimerRunning: Boolean = false,
    val hasCompletedOnboarding: Boolean = true,
    val userEmail: String = "student@fieldnote.app"
)

class FieldnoteViewModel(
    application: Application,
    injectedRepository: FieldnoteRepository? = null
) : AndroidViewModel(application) {

    val repository: FieldnoteRepository = injectedRepository
        ?: (application as? com.example.FieldnoteApplication)?.container?.repository
        ?: FieldnoteRepository(com.example.data.db.AppDatabase.getInstance(application).dao())

    private val _uiState = MutableStateFlow(FieldnoteUiState())
    val uiState: StateFlow<FieldnoteUiState>

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        val flow1 = combine(
            _uiState,
            repository.allNotes,
            repository.allTasks
        ) { state, notes, tasks ->
            Triple(state, notes, tasks)
        }

        val flow2 = combine(
            repository.chatMessages,
            repository.allHabits,
            repository.allFocusSessions
        ) { chatMessages, habits, focusSessions ->
            Triple(chatMessages, habits, focusSessions)
        }

        uiState = combine(flow1, flow2) { (state, notes, tasks), (chatMessages, habits, focusSessions) ->
            state.copy(
                notes = notes,
                tasks = tasks,
                chatMessages = chatMessages,
                habits = habits,
                focusSessions = focusSessions
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FieldnoteUiState()
        )
    }

    fun setScreen(screen: FieldnoteScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun toggleCopilot(open: Boolean? = null) {
        _uiState.update { it.copy(isCopilotOpen = open ?: !it.isCopilotOpen) }
    }

    fun toggleDarkTheme(enable: Boolean? = null) {
        _uiState.update { it.copy(isDarkTheme = enable ?: !it.isDarkTheme) }
    }

    fun setDefaultPriority(priority: String) {
        _uiState.update { it.copy(defaultPriority = priority) }
    }

    fun toggleNotifications(enabled: Boolean? = null) {
        _uiState.update { it.copy(notificationsEnabled = enabled ?: !it.notificationsEnabled) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // --- Search & Filters ---
    fun setSearchQuery(q: String) {
        _uiState.update { it.copy(searchQuery = q) }
    }

    fun setSelectedTagFilter(tag: String) {
        _uiState.update { it.copy(selectedTagFilter = tag) }
    }

    fun setTaskStatusFilter(status: String) {
        _uiState.update { it.copy(taskStatusFilter = status) }
    }

    fun setTaskPriorityFilter(priority: String) {
        _uiState.update { it.copy(taskPriorityFilter = priority) }
    }

    fun setTasksSortBy(sortBy: String) {
        _uiState.update { it.copy(tasksSortBy = sortBy) }
    }

    fun setNotesSortBy(sortBy: String) {
        _uiState.update { it.copy(notesSortBy = sortBy) }
    }

    fun toggleShowArchivedNotes() {
        _uiState.update { it.copy(showArchivedNotes = !it.showArchivedNotes) }
    }

    // --- Note Editor ---
    fun openNoteEditor(note: NoteEntity?) {
        _uiState.update {
            it.copy(
                activeEditingNote = note ?: NoteEntity(
                    id = UUID.randomUUID().toString(),
                    title = "",
                    content = "",
                    tags = ""
                ),
                isNoteEditorOpen = true
            )
        }
    }

    fun closeNoteEditor() {
        _uiState.update { it.copy(isNoteEditorOpen = false, activeEditingNote = null) }
    }

    fun saveNote(note: NoteEntity) {
        viewModelScope.launch {
            if (note.title.isNotBlank() || note.content.isNotBlank()) {
                repository.saveNote(note)
                showToast("Note saved")
            }
            closeNoteEditor()
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
            showToast("Note deleted")
            if (_uiState.value.activeEditingNote?.id == id) {
                closeNoteEditor()
            }
        }
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePinNote(note)
        }
    }

    fun toggleArchiveNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleArchiveNote(note)
            showToast(if (note.archived) "Note restored" else "Note archived")
        }
    }

    // --- Task Modal Sheet ---
    fun openTaskSheet(task: TaskEntity? = null) {
        val defaultDueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        _uiState.update {
            it.copy(
                activeEditingTask = task ?: TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = "",
                    priority = it.defaultPriority,
                    dueDate = defaultDueDate,
                    dueTime = "12:00"
                ),
                isTaskSheetOpen = true
            )
        }
    }

    fun closeTaskSheet() {
        _uiState.update { it.copy(isTaskSheetOpen = false, activeEditingTask = null) }
    }

    fun saveTask(task: TaskEntity, syncToCalendar: Boolean = false) {
        viewModelScope.launch {
            if (task.title.isNotBlank()) {
                var finalTask = task
                if (syncToCalendar) {
                    val eventId = CalendarSyncManager.syncTaskToDeviceCalendar(getApplication(), task)
                    finalTask = task.copy(syncedWithCalendar = true, calendarEventId = eventId)
                }
                repository.saveTask(finalTask)
                showToast("Task scheduled")

                if (_uiState.value.notificationsEnabled) {
                    ReminderNotificationManager.showTaskReminder(
                        getApplication(),
                        task.id.hashCode(),
                        finalTask.title,
                        "Due: ${finalTask.dueDate} ${finalTask.dueTime}",
                        finalTask.priority.uppercase()
                    )
                }
            }
            closeTaskSheet()
        }
    }

    fun deleteTask(id: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
            showToast("Task deleted")
            if (_uiState.value.activeEditingTask?.id == id) {
                closeTaskSheet()
            }
        }
    }

    fun cycleTaskStatus(task: TaskEntity) {
        viewModelScope.launch {
            repository.cycleTaskStatus(task)
        }
    }

    fun syncTaskToCalendar(task: TaskEntity) {
        viewModelScope.launch {
            val eventId = CalendarSyncManager.syncTaskToDeviceCalendar(getApplication(), task)
            val updated = task.copy(syncedWithCalendar = true, calendarEventId = eventId)
            repository.saveTask(updated)
            showToast("Synced to Calendar")
        }
    }

    // --- Gemini AI Copilot & Voice ---
    fun sendCopilotMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            sender = "user",
            content = userText
        )

        viewModelScope.launch {
            repository.saveChatMessage(userMsg)
            _uiState.update { it.copy(isCopilotThinking = true) }

            val reply = GeminiCopilotService.chat(
                userPrompt = userText,
                existingTasks = _uiState.value.tasks,
                existingNotesCount = _uiState.value.notes.size
            )

            val copilotMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                sender = "copilot",
                content = reply.text,
                suggestedAction = reply.suggestedAction,
                actionPayload = reply.actionTask?.let { task ->
                    "${task.title}|${task.priority}|${task.dueDate}|${task.dueTime}"
                }
            )
            repository.saveChatMessage(copilotMsg)
            _uiState.update { it.copy(isCopilotThinking = false) }
        }
    }

    fun executeCopilotAction(message: ChatMessageEntity) {
        viewModelScope.launch {
            if (message.suggestedAction == "CREATE_TASK" && message.actionPayload != null) {
                val parts = message.actionPayload.split("|")
                val title = parts.getOrNull(0) ?: "Scheduled Task"
                val priority = parts.getOrNull(1) ?: "medium"
                val dueDate = parts.getOrNull(2) ?: ""
                val dueTime = parts.getOrNull(3) ?: "12:00"

                val task = TaskEntity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = "Confirmed via Copilot action prompt.",
                    priority = priority,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    tags = "Copilot, AI"
                )
                repository.saveTask(task)
                showToast("Task added to checklist")
            }
        }
    }

    fun triggerVoiceInputSimulation(voiceCommand: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVoiceListening = true) }
            kotlinx.coroutines.delay(1200)
            _uiState.update { it.copy(isVoiceListening = false) }

            val command = voiceCommand ?: "Schedule team sync tomorrow at 3 PM with high priority"
            sendCopilotMessage(command)
        }
    }

    fun sendVoiceText(transcribedText: String) {
        sendCopilotMessage(transcribedText)
    }

    // --- Settings & Data Vault ---
    fun setMasterPassphrase(newPass: String) {
        CryptoManager.setMasterPassphrase(newPass)
        showToast("Master E2EE Passphrase updated")
    }

    fun triggerTestNotification() {
        ReminderNotificationManager.showTaskReminder(
            getApplication(),
            999,
            "Fieldnote Focus Reminder",
            "This is a verified real-time notification alert.",
            "HIGH"
        )
        showToast("Test notification sent")
    }

    fun exportData(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportDataJson()
            onResult(json)
            showToast("Export payload prepared")
        }
    }

    fun importData(jsonString: String) {
        viewModelScope.launch {
            val success = repository.importDataJson(jsonString)
            if (success) {
                showToast("Data imported successfully")
            } else {
                showToast("Import failed: Invalid JSON format")
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            showToast("All data wiped")
        }
    }

    // --- Natural Language Quick Add ---
    fun parseNaturalLanguageTask(input: String) {
        val parsed = com.example.data.nlp.NaturalLanguageTaskParser.parse(input)
        _uiState.update {
            it.copy(
                parsedNlTask = parsed,
                isNlConfirmOpen = true
            )
        }
    }

    fun confirmParsedTask(syncToCalendar: Boolean = false) {
        val parsed = _uiState.value.parsedNlTask ?: return
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = parsed.title,
            description = "Quick Add (${parsed.estimatedMinutes}m, ${parsed.category})",
            priority = parsed.priority,
            dueDate = parsed.dueDate,
            dueTime = parsed.dueTime,
            tags = parsed.category
        )
        saveTask(task, syncToCalendar)
        _uiState.update { it.copy(isNlConfirmOpen = false, parsedNlTask = null) }
        showToast("Task confirmed & added")
    }

    fun dismissParsedTask() {
        _uiState.update { it.copy(isNlConfirmOpen = false, parsedNlTask = null) }
    }

    // --- Focus Mode ---
    fun startFocusSession(minutes: Int) {
        _uiState.update {
            it.copy(
                currentFocusMinutes = minutes,
                isFocusTimerRunning = true
            )
        }
        showToast("Focus session started ($minutes min)")
    }

    fun completeFocusSession(taskTitle: String, durationMinutes: Int, mode: String = "pomodoro") {
        viewModelScope.launch {
            val session = com.example.data.model.FocusSessionEntity(
                id = UUID.randomUUID().toString(),
                taskTitle = if (taskTitle.isBlank()) "Deep Focus" else taskTitle,
                durationMinutes = durationMinutes,
                completed = true,
                mode = mode
            )
            repository.saveFocusSession(session)
            _uiState.update { it.copy(isFocusTimerRunning = false) }
            showToast("Focus session logged ($durationMinutes m)")
        }
    }

    fun cancelFocusTimer() {
        _uiState.update { it.copy(isFocusTimerRunning = false) }
    }

    // --- Habits ---
    fun toggleHabit(habit: com.example.data.model.HabitEntity) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
            repository.toggleHabitCompletion(habit, todayStr)
        }
    }

    fun addHabit(title: String, category: String, frequency: String, targetDays: Int, color: String = "#8FA378") {
        if (title.isBlank()) return
        viewModelScope.launch {
            val habit = com.example.data.model.HabitEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                frequency = frequency,
                targetDaysPerWeek = targetDays,
                color = color
            )
            repository.saveHabit(habit)
            showToast("Habit added")
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            repository.deleteHabit(id)
            showToast("Habit removed")
        }
    }

    // --- Privacy Vault ---
    fun unlockVault(passphrase: String): Boolean {
        return if (passphrase.trim() == "fieldnote" || passphrase.trim().length >= 4) {
            CryptoManager.setMasterPassphrase(passphrase)
            _uiState.update { it.copy(isVaultUnlocked = true) }
            showToast("Vault unlocked with AES-256-GCM")
            true
        } else {
            showToast("Passphrase too short (minimum 4 chars)")
            false
        }
    }

    fun lockVault() {
        _uiState.update { it.copy(isVaultUnlocked = false) }
        showToast("Vault secured")
    }

    // --- Onboarding ---
    fun finishOnboarding() {
        _uiState.update { it.copy(hasCompletedOnboarding = true, currentScreen = FieldnoteScreen.DASHBOARD) }
    }

    companion object {
        fun provideFactory(application: Application): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    val app = application as? com.example.FieldnoteApplication
                    val repo = app?.container?.repository
                    return FieldnoteViewModel(application, repo) as T
                }
            }
    }
}
