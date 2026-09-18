package com.example.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.data.db.AppDatabase
import com.example.data.db.FieldnoteDao
import com.example.data.repository.FieldnoteRepository
import com.example.data.repository.FirestoreTaskRepository
import com.example.data.repository.RoomTaskRepository
import com.example.data.repository.TaskRepository

/**
 * Dependency injection container interface defining application-wide dependencies.
 * Provides singleton instances of database and repositories throughout the app.
 */
interface AppContainer {
    val database: AppDatabase
    val dao: FieldnoteDao
    val localTaskRepository: TaskRepository
    val roomTaskRepository: TaskRepository
    val firestoreTaskRepository: TaskRepository
    val taskRepository: TaskRepository
    val repository: FieldnoteRepository
}

/**
 * Default implementation of AppContainer using lazy manual dependency injection.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val dao: FieldnoteDao by lazy {
        database.dao()
    }

    override val localTaskRepository: TaskRepository by lazy {
        com.example.data.repository.LocalTaskRepository(dao)
    }

    override val roomTaskRepository: TaskRepository by lazy {
        localTaskRepository
    }

    override val firestoreTaskRepository: TaskRepository by lazy {
        FirestoreTaskRepository(context = context)
    }

    override val taskRepository: TaskRepository by lazy {
        localTaskRepository
    }

    override val repository: FieldnoteRepository by lazy {
        FieldnoteRepository(
            dao = dao,
            localTaskRepository = roomTaskRepository,
            cloudTaskRepository = firestoreTaskRepository
        )
    }
}

/**
 * CompositionLocal providing access to the AppContainer throughout the Composable hierarchy.
 */
val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided via LocalAppContainer")
}
