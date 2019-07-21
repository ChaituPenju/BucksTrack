package com.chaitupenjudcoder.firebasehelpers;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chaitupenjudcoder.buckstrack.BucksTrackWidget;
import com.chaitupenjudcoder.intentservices.BucksIntentService;

public class BucksWidgetHelper {

    public static final String BALANCE_EXTRA = "balance";
    public static final String INCOME_EXTRA = "income";
    public static final String EXPENSE_EXTRA = "expense";

    FirebaseCategoriesHelper helper = new FirebaseCategoriesHelper();
    FirebaseTransactionsHelper tHelper = new FirebaseTransactionsHelper();

    public void callIntentService(Context cxt, String message) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(cxt);
        ComponentName thisWidget = new ComponentName(cxt,
                BucksTrackWidget.class);
        Intent intent = new Intent(cxt, BucksIntentService.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
        Log.e("BABA", message);
        if (message.equals("Last Income")) {
            tHelper.getALastTransaction(transaction -> helper.getCategoryTotal(total1 -> helper.getCategoryTotal(total -> {
                intent.putExtra(BALANCE_EXTRA, total - total1);
                intent.putExtra(INCOME_EXTRA, total);
                intent.putExtra(EXPENSE_EXTRA, total1);
                intent.putExtra("message", transaction);
                cxt.startService(intent);
            }, "income"), "expense"), "income");

        } else if (message.equals("Last Expense")) {
            tHelper.getALastTransaction(transaction -> helper.getCategoryTotal(total1 -> helper.getCategoryTotal(total -> {
                intent.putExtra(BALANCE_EXTRA, total - total1);
                intent.putExtra(INCOME_EXTRA, total);
                intent.putExtra(EXPENSE_EXTRA, total1);
                intent.putExtra("message", transaction);
                cxt.startService(intent);
            }, "income"), "expense"), "expense");
        }
    }
}
