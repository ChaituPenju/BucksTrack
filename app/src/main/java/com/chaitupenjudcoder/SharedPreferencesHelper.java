package com.chaitupenjudcoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.chaitupenjudcoder.buckstrack.R;

public class SharedPreferencesHelper {

    public static final String CURRENCY_KEY = "currency_key";
    public static final String DATE_FORMAT_KEY = "date_format_key";
    public static final String WIDGET_OPTION_KEY = "widget_option_key";

    SharedPreferences preferences;
    Context ctx;

    public SharedPreferencesHelper(Context ctx){
        this.ctx = ctx;
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public String getCurrencyPref(String defValue) {
        Resources res = ctx.getResources();
//        String s = getString(preferences.getString(CURRENCY_KEY, defValue), 4);
        return preferences.getString(CURRENCY_KEY, defValue);
    }


}
