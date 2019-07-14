package com.chaitupenjudcoder;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class BucksTrackOffline extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //  set the firebase database persistence to true for saving offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
