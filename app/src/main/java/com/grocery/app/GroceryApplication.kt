package com.grocery.app

import android.app.Application
import com.google.firebase.FirebaseApp

class GroceryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}