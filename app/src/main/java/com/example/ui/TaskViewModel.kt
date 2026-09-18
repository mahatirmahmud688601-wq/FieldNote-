package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.FieldnoteApplication
import com.example.data.model.Task
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * UI State representing the state of the task list for the user interface.
 */
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userMessage: String? = null,
    val searchQuery: String = "",
    val statusFilter: String = "all", // "all", "todo", "completed", "in_progress"
    val priorityFilter: String = "all", // "all", "high", "medium", "low"
    val sortBy: String = "dueDate" // "dueDate", "priority", "created", "title"
) {
    /**
     * Filtered and sorted tasks according to current query and filter state.
     */
    val filteredTasks: List<Task>
        get() {
            return tasks
                .filter { task ->
                    val matchesQuery = searchQuery.isBlank() ||
                        task.title.contains(searchQuery, ignoreCase = true) ||
                        task.description.contains(searchQuery, ignoreCase = true) ||
                        task.tags.contains(searchQuery, ignoreCase = true)

                    val matchesStatus = when (statusFilter.lowercase()) {
                        "all" -> true
                        "completed", "done" -> task.isCompleted || task.status == "completed"
                        "todo", "pending", "active" -> !task.isCompleted && task.status != "completed"
                        "in_progress" -> task.status == "in_progress"
                        else -> task.status.equals(statusFilter, ignoreCase = true)
                    }

                    val matchesPriority = when (priorityFilter.lowercase()) {
                        "all" -> true
                        else -> task.priority.equals(priorityFilter, ignoreCase = true)
                    }

                    matchesQuery && matchesStatus && matchesPriority
                }
                .sortedWith { a, b ->
                    when (sortBy) {
                        "priority" -> {
                            val priorityWeight = mapOf("high" to 3, "medium" to 2, "low" to 1)
                            (priorityWeight[b.priority.lowercase()] ?: 0)
                                .compareTo(priorityWeight[a.priority.lowercase()] ?: 0)
                        }
                        "title" -> a.title.compareTo(b.title, ignoreCase = true)
                        "created" -> b.createdAt.compareTo(a.createdAt)
                        else -> {
                            // "dueDate" default
                            if (a.dueDate.isBlank() && b.dueDate.isNotBlank()) 1
                            else if (a.dueDate.isNotBlank() && b.dueDate.isBlank()) -1
                            else a.dueDate.compareTo(b.dueDate)
                        }
                    }
                }
        }

    val completedCount: Int
        get() = tasks.count { it.isCompleted || it.status == "completed" }

    val pendingCount: Int
        get() = tasks.size - completedCount
}

/**
 * TaskViewModel (and TaskListViewModel) that utilizes the TaskRepository to manage
 * the reactive state of the task list for the UI, including functions to fetch, add,
 * toggle, and delete tasks.
 */
open class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    /**
     * Backwards-compatible / direct tasks StateFlow stream.
     */
    val tasks: StateFlow<List<Task>> = _uiState
        .map { it.tasks }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isLoading: StateFlow<Boolean> = _uiState
        .map { it.isLoading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private var fetchJob: Job? = null

    init {
        fetchTasks()
    }

    /**
     * Fetches and observes tasks from the TaskRepository stream.
     */
    fun fetchTasks() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            taskRepository.getTasks()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Failed to fetch tasks"
                        )
                    }
                }
                .collect { taskList ->
                    _uiState.update { current ->
                        current.copy(
                            tasks = taskList,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Adds an existing Task entity using the TaskRepository.
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.insertTask(task)
                _uiState.update { it.copy(userMessage = "Task created successfully") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to add task") }
            }
        }
    }

    /**
     * Overload helper to create and add a new Task by attributes.
     */
    fun addTask(
        title: String,
        description: String = "",
        priority: String = "medium",
        dueDate: String = "",
        dueTime: String = "",
        tags: String = "",
        isCompleted: Boolean = false
    ) {
        if (title.isBlank()) return
        val now = System.currentTimeMillis()
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            description = description.trim(),
            priority = priority.lowercase(),
            dueDate = dueDate,
            dueTime = dueTime,
            tags = tags,
            timestamp = now,
            isCompleted = isCompleted,
            status = if (isCompleted) "completed" else "todo",
            createdAt = now,
            updatedAt = now
        )
        addTask(task)
    }

    /**
     * Toggles the completion status of a Task (completed <-> todo) and persists to TaskRepository.
     */
    fun toggleTask(task: Task) {
        viewModelScope.launch {
            try {
                val newCompleted = !task.isCompleted
                val newStatus = if (newCompleted) "completed" else "todo"
                val updatedTask = task.copy(
                    isCompleted = newCompleted,
                    status = newStatus,
                    updatedAt = System.currentTimeMillis()
                )
                taskRepository.updateTask(updatedTask)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to toggle task") }
            }
        }
    }

    /**
     * Toggles a task by its unique identifier.
     */
    fun toggleTask(taskId: String) {
        val task = _uiState.value.tasks.find { it.id == taskId } ?: return
        toggleTask(task)
    }

    /**
     * Deletes a task from the repository by its unique identifier.
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
                _uiState.update { it.copy(userMessage = "Task deleted") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to delete task") }
            }
        }
    }

    /**
     * Deletes a task from the repository.
     */
    fun deleteTask(task: Task) {
        deleteTask(task.id)
    }

    /**
     * Updates an existing task with modified values.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to update task") }
            }
        }
    }

    /**
     * Cycles through task status (todo -> in_progress -> completed -> todo).
     */
    fun cycleTaskStatus(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.cycleTaskStatus(task)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to cycle task status") }
            }
        }
    }

    /**
     * Cycles through task status by task id.
     */
    fun cycleTaskStatus(taskId: String) {
        val task = _uiState.value.tasks.find { it.id == taskId } ?: return
        cycleTaskStatus(task)
    }

    /**
     * Deletes all completed tasks.
     */
    fun clearCompletedTasks() {
        viewModelScope.launch {
            try {
                val completed = _uiState.value.tasks.filter { it.isCompleted || it.status == "completed" }
                completed.forEach { taskRepository.deleteTask(it.id) }
                _uiState.update { it.copy(userMessage = "Cleared completed tasks") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "Failed to clear completed tasks") }
            }
        }
    }

    // --- Search & Filter Controls ---

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setStatusFilter(status: String) {
        _uiState.update { it.copy(statusFilter = status) }
    }

    fun setPriorityFilter(priority: String) {
        _uiState.update { it.copy(priorityFilter = priority) }
    }

    fun setSortBy(sortBy: String) {
        _uiState.update { it.copy(sortBy = sortBy) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    companion object {
        /**
         * Provides a ViewModelProvider.Factory with an explicit TaskRepository.
         */
        fun provideFactory(taskRepository: TaskRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(taskRepository) as T
                }
            }

        /**
         * Provides a ViewModelProvider.Factory resolving TaskRepository from application container.
         */
        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val app = context.applicationContext as? FieldnoteApplication
                    val repo = app?.container?.taskRepository
                        ?: com.example.data.repository.LocalTaskRepository(
                            com.example.data.db.AppDatabase.getInstance(context).dao()
                        )
                    return TaskViewModel(repo) as T
                }
            }
    }
}

/**
 * Typealias ensuring TaskListViewModel resolves to TaskViewModel for seamless API parity.
 */
typealias TaskListViewModel = TaskViewModel
