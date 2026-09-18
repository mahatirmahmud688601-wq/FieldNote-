package com.example

import com.example.data.crypto.CryptoManager
import com.example.data.nlp.NaturalLanguageTaskParser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldnoteLogicTest {

    @Test
    fun `natural language parser extracts title, date, and priority correctly`() {
        val input = "Finish Physics assignment next Friday at 4 PM, high priority, 90 minutes"
        val result = NaturalLanguageTaskParser.parse(input)

        assertEquals("high", result.priority)
        assertEquals("16:00", result.dueTime)
        assertEquals(90, result.estimatedMinutes)
        assertEquals("Study", result.category)
        assertTrue(result.dueDate.isNotBlank())
        assertTrue(result.title.contains("Physics assignment", ignoreCase = true))
    }

    @Test
    fun `crypto manager encrypts and decrypts with hardware AES-256-GCM`() {
        CryptoManager.setMasterPassphrase("fieldnote_secure_key_2026")
        val plainText = "Secret personal vault note about private architecture ideas"
        val cipherText = CryptoManager.encrypt(plainText)

        assertNotNull(cipherText)
        assertTrue(cipherText.contains(":")) // IV : CipherText format
        assertFalse(cipherText == plainText)

        val decrypted = CryptoManager.decrypt(cipherText)
        assertEquals(plainText, decrypted)
    }

    @Test
    fun `task model correctly converts between Room entity and Firestore mapping`() {
        val original = com.example.data.model.Task(
            id = "task-101",
            title = "Prepare research report",
            description = "Detailed breakdown of quarterly metrics",
            priority = "high",
            status = "in_progress",
            dueDate = "2026-09-10",
            dueTime = "14:30",
            tags = "Research,Work"
        )

        // Room Entity conversion roundtrip
        val entity = original.toEntity()
        assertEquals("task-101", entity.id)
        assertEquals("Prepare research report", entity.title)
        val fromEntity = com.example.data.model.Task.fromEntity(entity)
        assertEquals(original, fromEntity)

        // Firestore Map conversion roundtrip
        val firestoreMap = original.toFirestoreMap()
        assertEquals("Prepare research report", firestoreMap["title"])
        assertEquals("high", firestoreMap["priority"])
        val fromFirestore = com.example.data.model.Task.fromFirestoreMap(firestoreMap, "task-101")
        assertEquals(original.id, fromFirestore.id)
        assertEquals(original.title, fromFirestore.title)
        assertEquals(original.priority, fromFirestore.priority)
        assertEquals(original.status, fromFirestore.status)
        assertEquals(original.dueDate, fromFirestore.dueDate)
    }

    @Test
    fun `Task and TaskEntity have required fields for description, timestamp, and completion status`() {
        // Verify TaskEntity fields
        val entityInstance = com.example.data.model.TaskEntity(
            id = "test-id",
            title = "Testing Room Entity",
            description = "Entity description content",
            timestamp = 1700000000000L,
            isCompleted = true
        )
        assertEquals("Entity description content", entityInstance.description)
        assertEquals(1700000000000L, entityInstance.timestamp)
        assertTrue(entityInstance.isCompleted)
        assertTrue(entityInstance.isDone)
        assertEquals("completed", entityInstance.status)

        // Verify Task model fields and roundtrip
        val taskInstance = com.example.data.model.Task(
            id = "test-task",
            title = "Task Title",
            description = "Task description field",
            timestamp = 1700000000000L,
            isCompleted = true
        )
        assertEquals("Task description field", taskInstance.description)
        assertEquals(1700000000000L, taskInstance.timestamp)
        assertTrue(taskInstance.isCompleted)
        assertTrue(taskInstance.isDone)

        // Roundtrip to entity preserves description, timestamp, and completion status
        val convertedEntity = taskInstance.toEntity()
        assertEquals(taskInstance.description, convertedEntity.description)
        assertEquals(taskInstance.timestamp, convertedEntity.timestamp)
        assertTrue(convertedEntity.isCompleted)
        assertTrue(convertedEntity.isDone)

        val reconvertedTask = com.example.data.model.Task.fromEntity(convertedEntity)
        assertEquals(taskInstance.description, reconvertedTask.description)
        assertEquals(taskInstance.timestamp, reconvertedTask.timestamp)
        assertTrue(reconvertedTask.isCompleted)
    }

    @Test
    fun `AppDatabase class is defined extending RoomDatabase and typealias is compatible`() {
        assertTrue(androidx.room.RoomDatabase::class.java.isAssignableFrom(com.example.data.db.AppDatabase::class.java))
        assertEquals(com.example.data.db.AppDatabase::class.java, com.example.data.db.FieldnoteDatabase::class.java)
    }

    @Test
    fun `CharcoalOliveColorScheme defines dark minimalist charcoal and olive palette`() {
        val scheme = com.example.ui.theme.CharcoalOliveColorScheme
        // Primary is signature muted sage / olive green
        assertEquals(com.example.ui.theme.OliveGreenPrimary, scheme.primary)
        // Background and surface are matte charcoal
        assertEquals(com.example.ui.theme.CharcoalCanvas, scheme.background)
        assertEquals(com.example.ui.theme.CharcoalSurface, scheme.surface)
        // Contrast onPrimary is deep charcoal
        assertEquals(com.example.ui.theme.OliveGreenOnPrimary, scheme.onPrimary)
        // Outline is delicate hairline border
        assertEquals(com.example.ui.theme.CharcoalBorder, scheme.outline)
    }

    @Test
    fun `LocalTaskRepository is annotated with Hilt Singleton and Inject constructor`() {
        val clazz = com.example.data.repository.LocalTaskRepository::class.java
        assertTrue("LocalTaskRepository must be annotated with @Singleton", clazz.isAnnotationPresent(javax.inject.Singleton::class.java))
        val constructor = clazz.constructors.firstOrNull { it.isAnnotationPresent(javax.inject.Inject::class.java) }
        assertTrue("LocalTaskRepository must have an @Inject constructor for Hilt", constructor != null)
    }

    @Test
    fun `LocalTaskRepository performs full CRUD operations on user tasks`() {
        kotlinx.coroutines.runBlocking {
            // In-memory fake DAO for testing CRUD operations
            val tasksMap = mutableMapOf<String, com.example.data.model.TaskEntity>()
            val tasksFlow = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.data.model.TaskEntity>>(listOf())

            fun syncFlow() {
                tasksFlow.value = tasksMap.values.toList()
            }

            val fakeDao = object : com.example.data.db.FieldnoteDao {
                override fun getAllNotes() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.NoteEntity>>()
                override suspend fun getNoteById(id: String) = null
                override suspend fun insertNote(note: com.example.data.model.NoteEntity) {}
                override suspend fun insertNotes(notes: List<com.example.data.model.NoteEntity>) {}
                override suspend fun updateNote(note: com.example.data.model.NoteEntity) {}
                override suspend fun deleteNote(note: com.example.data.model.NoteEntity) {}
                override suspend fun deleteNoteById(id: String) {}
                override suspend fun deleteAllNotes() {}

                override fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<com.example.data.model.TaskEntity>> = tasksFlow
                override suspend fun getTaskById(id: String) = tasksMap[id]
                override fun observeTaskById(id: String) = kotlinx.coroutines.flow.flowOf(tasksMap[id])
                override suspend fun insertTask(task: com.example.data.model.TaskEntity) {
                    tasksMap[task.id] = task
                    syncFlow()
                }
                override suspend fun insertTasks(tasks: List<com.example.data.model.TaskEntity>) {
                    tasks.forEach { tasksMap[it.id] = it }
                    syncFlow()
                }
                override suspend fun updateTask(task: com.example.data.model.TaskEntity) {
                    tasksMap[task.id] = task
                    syncFlow()
                }
                override suspend fun deleteTask(task: com.example.data.model.TaskEntity) {
                    tasksMap.remove(task.id)
                    syncFlow()
                }
                override suspend fun deleteTaskById(id: String) {
                    tasksMap.remove(id)
                    syncFlow()
                }
                override suspend fun deleteAllTasks() {
                    tasksMap.clear()
                    syncFlow()
                }

                override fun getAllChatMessages() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.ChatMessageEntity>>()
                override suspend fun insertChatMessage(message: com.example.data.model.ChatMessageEntity) {}
                override suspend fun deleteAllChatMessages() {}

                override fun getAllHabits() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.HabitEntity>>()
                override suspend fun insertHabit(habit: com.example.data.model.HabitEntity) {}
                override suspend fun updateHabit(habit: com.example.data.model.HabitEntity) {}
                override suspend fun deleteHabitById(id: String) {}
                override suspend fun deleteAllHabits() {}

                override fun getAllFocusSessions() = kotlinx.coroutines.flow.emptyFlow<List<com.example.data.model.FocusSessionEntity>>()
                override suspend fun insertFocusSession(session: com.example.data.model.FocusSessionEntity) {}
                override suspend fun deleteAllFocusSessions() {}
            }

            val repository = com.example.data.repository.LocalTaskRepository(fakeDao)

            // CREATE
            val task1 = com.example.data.model.Task(id = "t1", title = "Write Unit Tests", priority = "high", status = "todo")
            val task2 = com.example.data.model.Task(id = "t2", title = "Refactor Database", priority = "medium", status = "todo")
            repository.insertTask(task1)
            repository.insertTasks(listOf(task2))

            // READ
            val readTask1 = repository.getTaskById("t1")
            assertNotNull(readTask1)
            assertEquals("Write Unit Tests", readTask1?.title)
            assertEquals("high", readTask1?.priority)

            val allTasks = repository.getTasks()
            assertEquals(2, allTasks.first().size)

            // UPDATE (cycle status)
            repository.cycleTaskStatus(readTask1!!)
            val updatedTask1 = repository.getTaskById("t1")
            assertEquals("in_progress", updatedTask1?.status)

            // Explicit update
            repository.updateTask(updatedTask1!!.copy(title = "Write Comprehensive Tests"))
            assertEquals("Write Comprehensive Tests", repository.getTaskById("t1")?.title)

            // DELETE by ID
            repository.deleteTask("t2")
            assertNull(repository.getTaskById("t2"))

            // DELETE all
            repository.deleteAllTasks()
            assertNull(repository.getTaskById("t1"))
        }
    }

    @Test
    fun `TaskViewModel utilizes TaskRepository to manage task list state, fetch, add, toggle, and delete tasks`() {
        runBlocking {
            val taskStore = mutableMapOf<String, com.example.data.model.Task>()
            val tasksFlow = kotlinx.coroutines.flow.MutableStateFlow<List<com.example.data.model.Task>>(emptyList())

            fun emitCurrent() {
                tasksFlow.value = taskStore.values.toList()
            }

            val fakeRepository = object : com.example.data.repository.TaskRepository {
                override fun getTasks(): kotlinx.coroutines.flow.Flow<List<com.example.data.model.Task>> = tasksFlow

                override suspend fun getTaskById(id: String): com.example.data.model.Task? = taskStore[id]

                override suspend fun saveTask(task: com.example.data.model.Task) {
                    taskStore[task.id] = task
                    emitCurrent()
                }

                override suspend fun deleteTask(id: String) {
                    taskStore.remove(id)
                    emitCurrent()
                }

                override suspend fun cycleTaskStatus(task: com.example.data.model.Task) {
                    val nextStatus = when (task.status) {
                        "todo" -> "in_progress"
                        "in_progress" -> "completed"
                        else -> "todo"
                    }
                    val isDone = nextStatus == "completed"
                    taskStore[task.id] = task.copy(status = nextStatus, isCompleted = isDone)
                    emitCurrent()
                }
            }

            // Seed initial task
            val seedTask = com.example.data.model.Task(
                id = "seed-1",
                title = "Initial Review",
                description = "Check pull requests",
                status = "todo",
                isCompleted = false
            )
            fakeRepository.saveTask(seedTask)

            val viewModel = com.example.ui.TaskViewModel(fakeRepository)

            // 1. FETCH / Initial state observation
            viewModel.fetchTasks()
            // Wait for flow to collect
            var iterations = 0
            while (viewModel.uiState.value.tasks.isEmpty() && iterations < 20) {
                kotlinx.coroutines.delay(50)
                iterations++
            }
            val stateAfterFetch = viewModel.uiState.value
            assertEquals(1, stateAfterFetch.tasks.size)
            assertEquals("Initial Review", stateAfterFetch.tasks.first().title)
            assertEquals(1, stateAfterFetch.pendingCount)
            assertEquals(0, stateAfterFetch.completedCount)

            // 2. ADD TASK (using object and overload)
            val newTask = com.example.data.model.Task(
                id = "task-2",
                title = "Design Architecture",
                description = "Draft system diagram",
                priority = "high",
                status = "todo"
            )
            viewModel.addTask(newTask)
            viewModel.addTask(
                title = "Write Documentation",
                description = "Update README with setup steps",
                priority = "low"
            )

            // Verify add reflected in repository and state
            iterations = 0
            while (viewModel.uiState.value.tasks.size < 3 && iterations < 20) {
                kotlinx.coroutines.delay(50)
                iterations++
            }
            assertEquals(3, viewModel.uiState.value.tasks.size)

            // 3. TOGGLE TASK
            val taskToToggle = viewModel.uiState.value.tasks.find { it.id == "task-2" }
            assertNotNull(taskToToggle)
            assertFalse(taskToToggle!!.isCompleted)

            viewModel.toggleTask(taskToToggle)
            iterations = 0
            while (viewModel.uiState.value.tasks.find { it.id == "task-2" }?.isCompleted != true && iterations < 20) {
                kotlinx.coroutines.delay(50)
                iterations++
            }
            val toggledTask = viewModel.uiState.value.tasks.find { it.id == "task-2" }
            assertTrue(toggledTask!!.isCompleted)
            assertEquals("completed", toggledTask.status)
            assertEquals(1, viewModel.uiState.value.completedCount)

            // Toggle back using ID
            viewModel.toggleTask("task-2")
            iterations = 0
            while (viewModel.uiState.value.tasks.find { it.id == "task-2" }?.isCompleted != false && iterations < 20) {
                kotlinx.coroutines.delay(50)
                iterations++
            }
            val untoggledTask = viewModel.uiState.value.tasks.find { it.id == "task-2" }
            assertFalse(untoggledTask!!.isCompleted)
            assertEquals("todo", untoggledTask.status)

            // 4. DELETE TASK (by task and by id)
            viewModel.deleteTask("seed-1")
            iterations = 0
            while (viewModel.uiState.value.tasks.any { it.id == "seed-1" } && iterations < 20) {
                kotlinx.coroutines.delay(50)
                iterations++
            }
            assertEquals(2, viewModel.uiState.value.tasks.size)
            assertNull(viewModel.uiState.value.tasks.find { it.id == "seed-1" })

            val taskToDelete = viewModel.uiState.value.tasks.find { it.id == "task-2" }
            assertNotNull(taskToDelete)
            viewModel.deleteTask(taskToDelete!!)
            iterations = 0
            while (viewModel.uiState.value.tasks.any { it.id == "task-2" } && iterations < 20) {
                kotlinx.coroutines.delay(50)
                iterations++
            }
            assertEquals(1, viewModel.uiState.value.tasks.size)

            // 5. Test typealias parity
            assertTrue(viewModel is com.example.ui.TaskListViewModel)
        }
    }
}
