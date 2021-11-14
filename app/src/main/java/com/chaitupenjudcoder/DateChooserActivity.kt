package com.chaitupenjudcoder

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivityDateChooserBinding
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper
import java.util.*

class DateChooserActivity : AppCompatActivity() {

    lateinit var dateChooser: ActivityDateChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dateChooser = DataBindingUtil.setContentView(this, R.layout.activity_date_chooser)

        dateChooser.etStartDate.setOnClickListener {
            initDatePickerDialog(
                dateChooser.etStartDate
            )
        }
        dateChooser.etEndDate.setOnClickListener {
            initDatePickerDialog(
                dateChooser.etEndDate
            )
        }

        dateChooser.btnGetTransactions.setOnClickListener {
            val startDate = dateChooser.etStartDate.text.toString()
            val endDate = dateChooser.etEndDate.text.toString()

            Intent(this, BucksTransactions::class.java).also { intent ->
                intent.putExtra(FirebaseTransactionsHelper.DATE_ONE_EXTRA, startDate)
                intent.putExtra(FirebaseTransactionsHelper.DATE_TWO_EXTRA, endDate)
                startActivity(intent)
            }
        }
    }

    private fun initDatePickerDialog(et: EditText) {
        val c = Calendar.getInstance()
        val dayOfMonth = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH]
        val year = c[Calendar.YEAR]
        val datePick = DatePickerDialog(
            this@DateChooserActivity,
            { _: DatePicker?, year1: Int, month1: Int, dayOfMonth1: Int ->
                //something
                c[year1, month1] = dayOfMonth1
                val date = SharedPreferencesHelper(this@DateChooserActivity).convertDate(c.time)
                et.setText(date)
            },
            year,
            month,
            dayOfMonth
        )
        datePick.show()
    }
}