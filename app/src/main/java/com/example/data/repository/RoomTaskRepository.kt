package com.example.data.repository

import com.example.data.db.FieldnoteDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local database task repository backed by Room.
 * Injected via Hilt or manual constructor injection.
 * Extends [LocalTaskRepository] to provide comprehensive CRUD operations.
 */
@Singleton
class RoomTaskRepository @Inject constructor(
    dao: FieldnoteDao
) : LocalTaskRepository(dao)

