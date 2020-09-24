package com.chaitupenjudcoder.firebasehelpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharedPreferencesHelper {

    public static final String CURRENCY_KEY = "currency_key";
    public static final String PREVIOUS_DATE_FORMAT_KEY = "previous_date_format_key";
    public static final String DATE_FORMAT_KEY = "date_format_key";
    public static final String WIDGET_OPTION_KEY = "widget_option_key";
    private static final int FIRST_TIME = 0;

    private SharedPreferences preferences;
    private Context ctx;

    public SharedPreferencesHelper(Context ctx) {
        this.ctx = ctx;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.ctx);
    }

    public String getCurrencyPref(String defValue) {
        return preferences.getString(CURRENCY_KEY, defValue);
    }

    public void setPreviousDateFormatPref() {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(PREVIOUS_DATE_FORMAT_KEY, getPreviousDateFormatPref("dd-MM-yyyy")).apply();
    }

    public String getPreviousDateFormatPref(String defValue) {
        return preferences.getString(PREVIOUS_DATE_FORMAT_KEY, defValue);
    }

    public String getDateFormatPref(String defValue) {
        return preferences.getString(DATE_FORMAT_KEY, defValue);
    }

    public String getWidgetOptionPref(String defValue) {
        return preferences.getString(WIDGET_OPTION_KEY, defValue);
    }

    public String convertDate(Date dt) {
        String datePreference = getDateFormatPref("dd-MM-yyyy");
        SimpleDateFormat format = new SimpleDateFormat(datePreference, Locale.ENGLISH);

        return format.format(dt);
    }

}
