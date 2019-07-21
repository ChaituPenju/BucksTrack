package com.chaitupenjudcoder.buckstrack;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chaitupenjudcoder.intentservices.BucksIntentService;

/**
 * Implementation of App Widget functionality.
 */
public class BucksTrackWidget extends AppWidgetProvider {
//    static RemoteViews views;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
//
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        views = new RemoteViews(context.getPackageName(), R.layout.bucks_track_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
//        updateAllTextViews();
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

//    public static void updateAllTextViews() {
//        views.setTextViewText(R.id.tv_widget_balance, "2500");
//        views.setTextViewText(R.id.tv_widget_income, "INCOME\n1500");
//        views.setTextViewText(R.id.tv_widget_expense, "EXPENSE\n1000");
//        views.setTextViewText(R.id.tv_widget_title, "The title");
//        views.setTextViewText(R.id.tv_widget_description, "The Description");
//        views.setTextViewText(R.id.tv_widget_amount, "444");
//        views.setTextViewText(R.id.tv_widget_category, "The Category");
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

