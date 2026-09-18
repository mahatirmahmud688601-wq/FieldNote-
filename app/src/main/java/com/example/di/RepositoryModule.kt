package com.example.di

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.FieldnoteDao
import com.example.data.repository.LocalTaskRepository
import com.example.data.repository.RoomTaskRepository
import com.example.data.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalTasks

/**
 * Hilt module that provides database instances (AppDatabase, FieldnoteDao)
 * across the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFieldnoteDao(database: AppDatabase): FieldnoteDao {
        return database.dao()
    }
}

/**
 * Hilt module that binds the [LocalTaskRepository] implementation to [TaskRepository].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        localTaskRepository: LocalTaskRepository
    ): TaskRepository
}
