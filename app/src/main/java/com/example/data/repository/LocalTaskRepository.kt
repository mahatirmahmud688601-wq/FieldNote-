package com.example.data.repository

import com.example.data.db.FieldnoteDao
import com.example.data.model.Task
import com.example.data.model.toTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TaskRepository implementation that handles local database operations via Room,
 * injected via Hilt (or constructor injection), to manage full CRUD operations for user tasks.
 */
@Singleton
open class LocalTaskRepository @Inject constructor(
    private val dao: FieldnoteDao
) : TaskRepository {

    /**
     * READ: Reactive stream of all tasks ordered by dueDate, priority, and creation time.
     */
    override fun getTasks(): Flow<List<Task>> {
        return dao.getAllTasks().map { entities ->
            entities.map { it.toTask() }
        }
    }

    /**
     * READ: Fetch a single task by its unique ID.
     */
    override suspend fun getTaskById(id: String): Task? = withContext(Dispatchers.IO) {
        dao.getTaskById(id)?.toTask()
    }

    /**
     * READ: Reactive stream of a single task by its unique ID.
     */
    override fun getTaskByIdFlow(id: String): Flow<Task?> {
        return dao.observeTaskById(id).map { it?.toTask() }
    }

    /**
     * CREATE / UPDATE: Persist a task (insert or replace).
     */
    override suspend fun saveTask(task: Task): Unit = withContext(Dispatchers.IO) {
        val resolvedId = task.id.ifBlank { "task_${System.currentTimeMillis()}" }
        val updatedTask = task.copy(
            id = resolvedId,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertTask(updatedTask.toEntity())
    }

    /**
     * CREATE: Explicit insert of a single task.
     */
    override suspend fun insertTask(task: Task): Unit = saveTask(task)

    /**
     * CREATE: Bulk insert multiple tasks.
     */
    override suspend fun insertTasks(tasks: List<Task>): Unit = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val entities = tasks.map { task ->
            val resolvedId = task.id.ifBlank { "task_${System.nanoTime()}" }
            task.copy(id = resolvedId, updatedAt = now).toEntity()
        }
        dao.insertTasks(entities)
    }

    /**
     * UPDATE: Explicit update of an existing task entity.
     */
    override suspend fun updateTask(task: Task): Unit = withContext(Dispatchers.IO) {
        dao.updateTask(task.toEntity().copy(updatedAt = System.currentTimeMillis()))
    }

    /**
     * UPDATE: Advance task status in cycle: todo -> in_progress -> completed.
     */
    override suspend fun cycleTaskStatus(task: Task): Unit = withContext(Dispatchers.IO) {
        val nextStatus = when (task.status) {
            "todo" -> "in_progress"
            "in_progress" -> "completed"
            else -> "todo"
        }
        val updated = task.copy(status = nextStatus, updatedAt = System.currentTimeMillis())
        dao.updateTask(updated.toEntity())
    }

    /**
     * DELETE: Remove a task by unique ID.
     */
    override suspend fun deleteTask(id: String): Unit = withContext(Dispatchers.IO) {
        dao.deleteTaskById(id)
    }

    /**
     * DELETE: Remove a task by entity object.
     */
    override suspend fun deleteTask(task: Task): Unit = withContext(Dispatchers.IO) {
        dao.deleteTask(task.toEntity())
    }

    /**
     * DELETE: Delete all tasks from the local database.
     */
    override suspend fun deleteAllTasks(): Unit = withContext(Dispatchers.IO) {
        dao.deleteAllTasks()
    }
}
