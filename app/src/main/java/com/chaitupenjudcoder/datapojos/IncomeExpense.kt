package com.chaitupenjudcoder.datapojos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class IncomeExpense(
    var title: String,
    var amount: String,
    var date: String,
    var note: String,
    var category: String,
    var bucksString: String,
    var id: String? = "0"
): Parcelable {
    private var dateFormatFromPreference: String? = null

    // Default No-Arg Constructor for Firebase
    constructor() : this("", "",
        "", "", "",
        "", ""
    )

    fun setDateFormat(dateFormatFromPreference: String?) {
        this.dateFormatFromPreference = dateFormatFromPreference
    }

    val formattedDate: String
        get() {
            val format1 = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            val format2 = SimpleDateFormat(dateFormatFromPreference, Locale.ENGLISH)
            var date: Date? = null
            try {
                date = format1.parse(this.date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return format2.format(date)
        }
}