package com.chaitupenjudcoder.intentservices

import android.app.IntentService
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.datapojos.IncomeExpense
import com.chaitupenjudcoder.firebasehelpers.BucksWidgetHelper

class BucksIntentService : IntentService("BucksIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(
            applicationContext
        )
        val allWidgetIds = intent?.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
        val ie: IncomeExpense = intent?.getParcelableExtra("message")!!
        for (widgetId in allWidgetIds!!) {
            val remoteViews = RemoteViews(
                this
                    .applicationContext.packageName,
                R.layout.bucks_track_widget
            )
            // Set the text
            val balance = intent.getIntExtra(BucksWidgetHelper.BALANCE_EXTRA, 0)
            val income = intent.getIntExtra(BucksWidgetHelper.INCOME_EXTRA, 0)
            val expense = intent.getIntExtra(BucksWidgetHelper.EXPENSE_EXTRA, 0)
            remoteViews.setTextViewText(R.id.tv_widget_balance, "" + balance)
            remoteViews.setTextViewText(R.id.tv_widget_income, "INCOME\n$income")
            remoteViews.setTextViewText(R.id.tv_widget_expense, "EXPENSE\n$expense")
            updateAllTexts(remoteViews, ie)
            Log.d("BABA", ie.bucksString)
            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
        stopSelf()
    }

    fun updateAllTexts(rv: RemoteViews, ie: IncomeExpense?) {
        val incExp: String = if (ie!!.bucksString == "income") {
            "Last Income"
        } else {
            "Last Expense"
        }
        rv.setTextViewText(R.id.tv_last_income_expense, incExp)
        rv.setTextViewText(R.id.tv_widget_amount, ie.amount)
        rv.setTextViewText(R.id.tv_widget_title, ie.title)
        rv.setTextViewText(R.id.tv_widget_category, ie.category)
        rv.setTextViewText(R.id.tv_widget_description, ie.note)
    }
}