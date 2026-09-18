package com.example

import android.app.Application
import com.example.di.AppContainer
import com.example.di.DefaultAppContainer

/**
 * Application class initializing and maintaining the application-level Dependency Injection container.
 */
class FieldnoteApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (e: Throwable) {
            android.util.Log.w("FieldnoteApplication", "FirebaseApp initialize: ${e.message}")
        }
        container = DefaultAppContainer(this)
        instance = this
    }

    companion object {
        lateinit var instance: FieldnoteApplication
            private set
    }
}
