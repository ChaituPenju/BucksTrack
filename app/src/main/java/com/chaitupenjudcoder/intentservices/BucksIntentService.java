package com.chaitupenjudcoder.intentservices;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.datapojos.IncomeExpense;

import static com.chaitupenjudcoder.firebasehelpers.BucksWidgetHelper.BALANCE_EXTRA;
import static com.chaitupenjudcoder.firebasehelpers.BucksWidgetHelper.EXPENSE_EXTRA;
import static com.chaitupenjudcoder.firebasehelpers.BucksWidgetHelper.INCOME_EXTRA;

public class BucksIntentService extends IntentService {
    public BucksIntentService() {
        super("BucksIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        IncomeExpense ie = intent.getParcelableExtra("message");

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(this
                    .getApplicationContext().getPackageName(),
                    R.layout.bucks_track_widget);
            // Set the text
            int balance = intent.getIntExtra(BALANCE_EXTRA, 0);
            int income = intent.getIntExtra(INCOME_EXTRA, 0);
            int expense = intent.getIntExtra(EXPENSE_EXTRA, 0);

            remoteViews.setTextViewText(R.id.tv_widget_balance, "" + balance);
            remoteViews.setTextViewText(R.id.tv_widget_income, "INCOME\n" + income);
            remoteViews.setTextViewText(R.id.tv_widget_expense, "EXPENSE\n" + expense);

            updateAllTexts(remoteViews, ie);
            Log.d("BABA", ie.getBucksString());

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();
    }

    public void updateAllTexts(RemoteViews rv, IncomeExpense ie) {
        String incExp;
        if (ie.getBucksString().equals("income")) {
            incExp = "Last Income";
        } else {
            incExp = "Last Expense";
        }
        rv.setTextViewText(R.id.tv_last_income_expense, incExp);
        rv.setTextViewText(R.id.tv_widget_amount, ie.getAmount());
        rv.setTextViewText(R.id.tv_widget_title, ie.getTitle());
        rv.setTextViewText(R.id.tv_widget_category, ie.getCategory());
        rv.setTextViewText(R.id.tv_widget_description, ie.getNote());
    }
}
