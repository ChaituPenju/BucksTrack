package com.chaitupenjudcoder;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.preffrags.SettingsPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (findViewById(R.id.settings_fragment_container) != null) {
            if (savedInstanceState != null)
                return;

            getFragmentManager().beginTransaction().add(R.id.settings_fragment_container, new SettingsPreference()).commit();
        }
    }
}
