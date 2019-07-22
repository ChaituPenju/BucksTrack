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
    public static final String DATE_FORMAT_KEY = "date_format_key";
    public static final String WIDGET_OPTION_KEY = "widget_option_key";

    private SharedPreferences preferences;
    private Context ctx;

    public SharedPreferencesHelper(Context ctx) {
        this.ctx = ctx;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.ctx);
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

    public String convertDate(String dateStr) {
        String datePreference = getDateFormatPref("dd-MM-yyyy");
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat format2 = new SimpleDateFormat(datePreference, Locale.ENGLISH);
        Date date = null;
        try {
            date = format1.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format2.format(date);
    }

}
