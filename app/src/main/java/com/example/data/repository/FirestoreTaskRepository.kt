package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Cloud task repository backed by Firebase Firestore.
 * Supports real-time snapshot synchronization and cloud task persistence.
 * Lazily and safely checks for Firebase initialization so the app remains fully functional
 * offline and when Firebase credentials (google-services.json) are not yet configured.
 */
class FirestoreTaskRepository(
    private val context: Context? = null,
    private val collectionName: String = "tasks"
) : TaskRepository {

    private val tag = "FirestoreTaskRepo"

    private fun getFirestoreInstance(): FirebaseFirestore? {
        return try {
            val app = try {
                FirebaseApp.getInstance()
            } catch (e: IllegalStateException) {
                // Not initialized yet, attempt initialization with provided context
                context?.let { FirebaseApp.initializeApp(it.applicationContext) }
            }

            if (app != null) {
                FirebaseFirestore.getInstance(app)
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.w(tag, "Firestore not available or not configured: ${e.message}")
            null
        }
    }

    override fun getTasks(): Flow<List<Task>> = callbackFlow {
        val firestore = getFirestoreInstance()
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        try {
            val collection = firestore.collection(collectionName)
                .orderBy("updatedAt", Query.Direction.DESCENDING)

            val registration = collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "Error listening for Firestore tasks: ${error.message}", error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    Task.fromFirestoreMap(data, doc.id)
                } ?: emptyList()

                trySend(tasks)
            }

            awaitClose {
                registration.remove()
            }
        } catch (e: Throwable) {
            Log.e(tag, "Failed to listen to collection $collectionName: ${e.message}", e)
            trySend(emptyList())
            close()
        }
    }

    override suspend fun saveTask(task: Task): Unit = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestoreInstance() ?: return@withContext

            val docId = task.id.ifBlank { firestore.collection(collectionName).document().id }
            val taskToSave = task.copy(id = docId, updatedAt = System.currentTimeMillis())
            firestore.collection(collectionName)
                .document(docId)
                .set(taskToSave.toFirestoreMap())
                .await()
        } catch (e: Throwable) {
            Log.e(tag, "Failed to save task to Firestore: ${e.message}", e)
        }
    }

    override suspend fun deleteTask(id: String): Unit = withContext(Dispatchers.IO) {
        try {
            val firestore = getFirestoreInstance() ?: return@withContext

            firestore.collection(collectionName)
                .document(id)
                .delete()
                .await()
        } catch (e: Throwable) {
            Log.e(tag, "Failed to delete task from Firestore: ${e.message}", e)
        }
    }

    override suspend fun cycleTaskStatus(task: Task): Unit = withContext(Dispatchers.IO) {
        val nextStatus = when (task.status) {
            "todo" -> "in_progress"
            "in_progress" -> "completed"
            else -> "todo"
        }
        val updated = task.copy(status = nextStatus, updatedAt = System.currentTimeMillis())
        saveTask(updated)
    }
}
