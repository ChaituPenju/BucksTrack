package com.chaitupenjudcoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {

    public static final String CURRENCY_KEY = "currency_key";
    public static final String DATE_FORMAT_KEY = "date_format_key";
    public static final String WIDGET_OPTION_KEY = "widget_option_key";

    private SharedPreferences preferences;
    private Context ctx;

    public SharedPreferencesHelper(Context ctx) {
        this.ctx = ctx;
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public String getCurrencyPref(String defValue) {
        return preferences.getString(CURRENCY_KEY, defValue);
    }

    public String getDateFormatPref(String defValue) {
        return preferences.getString(DATE_FORMAT_KEY, defValue);
    }

    public String getWidgetOptionPref(String defValue) {
        return preferences.getString(WIDGET_OPTION_KEY, defValue);
    }


}
