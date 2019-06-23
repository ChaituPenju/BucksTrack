package com.chaitupenjudcoder.preffrags;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.chaitupenjudcoder.buckstrack.R;

public class SettingsPreference extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.bucks_preferences);
    }
}
