package com.chaitupenjudcoder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivityAddIncomeExpenseBinding
import com.chaitupenjudcoder.datapojos.IncomeExpense
import com.chaitupenjudcoder.firebasehelpers.BucksInputValidationHelper
import com.chaitupenjudcoder.firebasehelpers.FirebaseCategoriesHelper
import com.chaitupenjudcoder.firebasehelpers.FirebaseTransactionsHelper
import com.chaitupenjudcoder.firebasehelpers.SharedPreferencesHelper
import java.util.*

class AddIncomeExpenseActivity : AppCompatActivity() {
    private lateinit var incomeExpenseBinding: ActivityAddIncomeExpenseBinding
    private var isIncome = false
    lateinit var BUCKS_STRING: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        incomeExpenseBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_income_expense)

        //  set default value for bucks string
        BUCKS_STRING = "income"
        intent.extras?.let { mExtras ->
            if (mExtras.containsKey(BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA)) {
                isIncome = mExtras.getBoolean(BucksActivity.BUCKS_STRING_IS_INCOME_EXTRA)
                BUCKS_STRING = if (isIncome) "income" else "expense"
            }
            if (mExtras.containsKey(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA)) {
                val ie: IncomeExpense = intent.getParcelableExtra(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA)!!
                incomeExpenseBinding.incExpTransac = ie
            }
        }
        incomeExpenseBinding.isBucksIncome = isIncome

        //  initialize date picker dialog with current date and update selected date
        incomeExpenseBinding.etDate.setOnClickListener { initDatePickerDialog() }
        val categoryHelper = FirebaseCategoriesHelper()
        categoryHelper.getAllCategories({ categoriesList: ArrayList<String> ->
            setSpinnerAdapter(
                categoriesList
            )
        }, BUCKS_STRING)
    }

    private fun setSpinnerAdapter(categoriesList: ArrayList<String>) {
        var categorie: Array<String?>? = arrayOfNulls(categoriesList.size)
        categorie = categoriesList.toArray(categorie)
        val cats =
            ArrayAdapter(application, android.R.layout.simple_spinner_dropdown_item, categorie)
        incomeExpenseBinding.spiCategories.adapter = cats
        if (intent.extras!!.containsKey(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA)) {
            val ie: IncomeExpense =
                intent.getParcelableExtra(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA)!!
            setSelectSpinnerValue(ie.category)
        }
    }

    private fun setSelectSpinnerValue(category: String) {
        val spinner = incomeExpenseBinding.spiCategories
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == category) {
                spinner.setSelection(i)
                break
            }
        }
    }

    private fun initDatePickerDialog() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(
            this@AddIncomeExpenseActivity,
            { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                incomeExpenseBinding.etDate.setText(
                    resources.getString(R.string.date_format_string, dayOfMonth, month + 1, year)
                )
            },
            mYear,
            mMonth,
            mDay
        )
        dialog.show()
    }

    fun updateIncomeExpenseData(incExp: IncomeExpense?) {
        val key = incomeExpenseBinding.incExpTransac?.id!!
        val tHelper = FirebaseTransactionsHelper()
        tHelper.updateATransaction({ response: String? ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            finish()
        }, key, incExp)
    }

    fun saveIncomeExpenseData(view: View?) {
        var titleStr: String
        var amountStr: String
        var dateStr: String
        var descriptionStr: String
        var categoryStr: String

        incomeExpenseBinding.apply ieBinding@{
            titleStr = this@ieBinding.etTitle.text.toString()
            amountStr = this@ieBinding.etAmount.text.toString()
            dateStr = this@ieBinding.etDate.text.toString()
            descriptionStr = this@ieBinding.etDescription.text.toString()
            categoryStr = this@ieBinding.spiCategories.selectedItem as String
        }

        val validationHelper = BucksInputValidationHelper()

        //  input validation
        if (!validationHelper.inputValidator(
                incomeExpenseBinding.etTitleWrapper,
                incomeExpenseBinding.etTitleWrapper.counterMaxLength
            ) or !validationHelper.inputValidator(
                incomeExpenseBinding.etAmountWrapper,
                incomeExpenseBinding.etAmountWrapper.counterMaxLength
            ) or !validationHelper.inputValidator(
                incomeExpenseBinding.etDateWrapper,
                10
            ) or !validationHelper.inputValidator(
                incomeExpenseBinding.etDescriptionWrapper,
                incomeExpenseBinding.etDescriptionWrapper.counterMaxLength
            )
        ) {
            return
        }

        //  create an instance of addincomeexpense object to push
        val incExp = IncomeExpense(
            titleStr,
            amountStr,
            dateStr,
            descriptionStr,
            categoryStr,
            BUCKS_STRING
        )
        incExp.setDateFormat(
            SharedPreferencesHelper(this@AddIncomeExpenseActivity).getDateFormatPref(
                "dd-MM-yyyy"
            )
        )

        //  update transaction if it shows from update
        if (intent.extras != null && intent.extras!!.containsKey(BucksActivity.INCOME_EXPENSE_OBJECT_EXTRA)) {
            updateIncomeExpenseData(incExp)
        } else {
            FirebaseTransactionsHelper().addATransaction({ response: String? ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                finish()
            }, incExp)
        }
    }
}