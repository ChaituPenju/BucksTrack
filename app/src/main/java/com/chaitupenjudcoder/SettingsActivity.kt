package com.chaitupenjudcoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.preffrags.SettingsPreference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        if (findViewById<View?>(R.id.settings_fragment_container) != null) {
            if (savedInstanceState != null) return
            supportFragmentManager.beginTransaction()
                .add(R.id.settings_fragment_container, SettingsPreference() as Fragment).commit()
        }
    }
}