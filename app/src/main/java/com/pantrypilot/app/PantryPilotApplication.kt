package com.pantrypilot.app

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PantryPilotApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val app = FirebaseApp.initializeApp(this)
        if (app != null) {
            Log.d(TAG, "Firebase initialized successfully: ${app.name}")
            val appCheckFactory = if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                PlayIntegrityAppCheckProviderFactory.getInstance()
            }
            Firebase.appCheck.installAppCheckProviderFactory(appCheckFactory)
            Log.d(TAG, "Firebase App Check initialized successfully")
        } else {
            Log.w(TAG, "Firebase initialization returned null")
        }
    }

    companion object {
        private const val TAG = "PantryPilotApp"
    }
}
