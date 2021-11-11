package com.chaitupenjudcoder.firebasehelpers

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class SharedPreferencesHelper(ctx: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

    fun getCurrencyPref(defValue: String?): String? {
        return preferences.getString(CURRENCY_KEY, defValue)
    }

    fun setPreviousDateFormatPref() {
        val prefEditor = preferences.edit()
        prefEditor.putString(PREVIOUS_DATE_FORMAT_KEY, getPreviousDateFormatPref("dd-MM-yyyy"))
            .apply()
    }

    private fun getPreviousDateFormatPref(defValue: String?): String? {
        return preferences.getString(PREVIOUS_DATE_FORMAT_KEY, defValue)
    }

    fun getDateFormatPref(defValue: String?): String? {
        return preferences.getString(DATE_FORMAT_KEY, defValue)
    }

    fun getWidgetOptionPref(defValue: String?): String? {
        return preferences.getString(WIDGET_OPTION_KEY, defValue)
    }

    fun convertDate(dt: Date?): String {
        val datePreference = getDateFormatPref("dd-MM-yyyy")
        val format = SimpleDateFormat(datePreference, Locale.ENGLISH)
        return format.format(dt)
    }

    companion object {
        const val CURRENCY_KEY = "currency_key"
        const val PREVIOUS_DATE_FORMAT_KEY = "previous_date_format_key"
        const val DATE_FORMAT_KEY = "date_format_key"
        const val WIDGET_OPTION_KEY = "widget_option_key"
    }

}