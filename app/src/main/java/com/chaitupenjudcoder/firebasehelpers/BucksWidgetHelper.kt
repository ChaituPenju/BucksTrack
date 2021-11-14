package com.chaitupenjudcoder.firebasehelpers

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.chaitupenjudcoder.buckstrack.BucksTrackWidget
import com.chaitupenjudcoder.datapojos.IncomeExpense
import com.chaitupenjudcoder.intentservices.BucksIntentService

class BucksWidgetHelper {
    var helper = FirebaseCategoriesHelper()
    var tHelper = FirebaseTransactionsHelper()

    fun callIntentService(cxt: Context, message: String) {
//        val appWidgetManager = AppWidgetManager.getInstance(cxt)
//        val thisWidget = ComponentName(cxt, BucksTrackWidget::class.java)
//        val intent = Intent(cxt, BucksIntentService::class.java)
//        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds)
//        Log.e("BABA", message)
//        if (message == "Last Income") {
//            tHelper.getALastTransaction({ transaction: IncomeExpense? ->
//                helper.getCategoryTotal(
//                    { total1: Int ->
//                        helper.getCategoryTotal({ total: Int ->
//                            intent.putExtra(BALANCE_EXTRA, total - total1)
//                            intent.putExtra(INCOME_EXTRA, total)
//                            intent.putExtra(EXPENSE_EXTRA, total1)
//                            intent.putExtra("message", transaction)
//                            cxt.startService(intent)
//                        }, "income")
//                    }, "expense"
//                )
//            }, "income")
//        } else if (message == "Last Expense") {
//            tHelper.getALastTransaction({ transaction: IncomeExpense? ->
//                helper.getCategoryTotal(
//                    { total1: Int ->
//                        helper.getCategoryTotal({ total: Int ->
//                            intent.putExtra(BALANCE_EXTRA, total - total1)
//                            intent.putExtra(INCOME_EXTRA, total)
//                            intent.putExtra(EXPENSE_EXTRA, total1)
//                            intent.putExtra("message", transaction)
//                            cxt.startService(intent)
//                        }, "income")
//                    }, "expense"
//                )
//            }, "expense")
//        }
    }

    companion object {
        const val BALANCE_EXTRA = "balance"
        const val INCOME_EXTRA = "income"
        const val EXPENSE_EXTRA = "expense"
    }
}