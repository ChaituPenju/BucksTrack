package com.chaitupenjudcoder.preffrags;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsPreference extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener bucksPreferenceListener;

    public static final String CURRENCY_KEY = "currency_key";
    public static final String DATE_FORMAT_KEY = "date_format_key";
    public static final String WIDGET_OPTION_KEY = "widget_option_key";

    Preference currencyPref, dateFormatPref, widgetOptionPref;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.bucks_preferences);

        //  initialize all the preference objects
        currencyPref = findPreference(CURRENCY_KEY);
        dateFormatPref = findPreference(DATE_FORMAT_KEY);
        widgetOptionPref = findPreference(WIDGET_OPTION_KEY);


        bucksPreferenceListener = (sharedPreferences, key) -> {
            if (key.equals(CURRENCY_KEY)) {
                currencyPref.setSummary(sharedPreferences.getString(key, "what") + " currency selected");
            }
            if (key.equals(DATE_FORMAT_KEY)) {
                dateFormatPref.setSummary(sharedPreferences.getString(key, "dd-mm-yyyy") + " format selected");
            }
            if (key.equals(WIDGET_OPTION_KEY)) {
                widgetOptionPref.setSummary(sharedPreferences.getString(key, "Last Expense") + " selected");
            }
        };

        String userId = "";
        //get the current user from auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            userId = mUser.getUid();
        }
        setLastLogin(userId);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(bucksPreferenceListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        retainPreferenceSummaries();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(bucksPreferenceListener);
    }

    private void retainPreferenceSummaries() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        currencyPref.setSummary(sharedPreferences.getString(CURRENCY_KEY, "₹") + " currency selected");
        dateFormatPref.setSummary(sharedPreferences.getString(DATE_FORMAT_KEY, "dd-mm-yyyy") + " format selected");
        widgetOptionPref.setSummary(sharedPreferences.getString(WIDGET_OPTION_KEY, "Last Expense") + " selected");
    }

    //function to set last login for a preference
    private void setLastLogin(String userId) {
        //get reference of path you want
        FirebaseDatabase mBase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mBase.getReference("users/" + userId + "/last_login");
        //add single value listener
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String last_login = dataSnapshot.getValue(String.class);
                Preference preference = getPreferenceScreen().findPreference("last_login_key");
                // epoch should be in milliseconds
                long dv = System.currentTimeMillis();
                if (last_login != null) {
                    dv = Long.valueOf(last_login) * 1000;
                }
                //convert epoch to date
                Date df = new java.util.Date(dv);
                //use simpledateformat function to format date from above date object
                String last_login_date = new SimpleDateFormat("dd-MM-yyyy hh:mma", Locale.ENGLISH).format(df);
                preference.setSummary("Last Login : " + last_login_date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //handle errors
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
