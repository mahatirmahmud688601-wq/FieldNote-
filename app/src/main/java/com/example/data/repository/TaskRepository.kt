package com.example.data.repository

import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining persistent task repository operations.
 * Implemented by local database repository and cloud Firestore repository patterns.
 */
interface TaskRepository {
    /**
     * Observable stream of all tasks.
     */
    fun getTasks(): Flow<List<Task>>

    /**
     * Retrieve a single task by its unique identifier.
     */
    suspend fun getTaskById(id: String): Task? = null

    /**
     * Observable stream of a single task by its unique identifier.
     */
    fun getTaskByIdFlow(id: String): Flow<Task?>? = null

    /**
     * Persist (insert or update) a task.
     */
    suspend fun saveTask(task: Task)

    /**
     * Explicit insert of a single user task (Create).
     */
    suspend fun insertTask(task: Task) = saveTask(task)

    /**
     * Bulk insert of user tasks (Create).
     */
    suspend fun insertTasks(tasks: List<Task>) {
        tasks.forEach { saveTask(it) }
    }

    /**
     * Explicit update of an existing task (Update).
     */
    suspend fun updateTask(task: Task) = saveTask(task)

    /**
     * Delete a task by its unique identifier (Delete).
     */
    suspend fun deleteTask(id: String)

    /**
     * Delete a task entity instance (Delete).
     */
    suspend fun deleteTask(task: Task) = deleteTask(task.id)

    /**
     * Remove all tasks from storage (Delete).
     */
    suspend fun deleteAllTasks() {}

    /**
     * Advance task status (e.g. todo -> in_progress -> completed).
     */
    suspend fun cycleTaskStatus(task: Task)
}
