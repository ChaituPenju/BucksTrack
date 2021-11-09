package com.chaitupenjudcoder

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class BucksTrackOffline : Application() {
    override fun onCreate() {
        super.onCreate()

        //  set the firebase database persistence to true for saving offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}