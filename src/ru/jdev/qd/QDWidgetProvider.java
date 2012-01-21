/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *
 */

package ru.jdev.qd;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class QDWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = "QD.WP";

    private static final String[] names = {
            "Мариночка Воловая", "Макс Васильев",
            "Мама", "Такси Грин",
            "Мегафон Балан", "Влад Цой",
            "Учебный отдел", "Евгений Коврижников"};

    private static final int[] imageIds = {R.drawable.prev, R.drawable.ic_contact_picture, R.drawable.icon};

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(LOG_TAG, "onUpdate");

        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            Log.v(LOG_TAG, "update widget " + appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            views.setViewVisibility(R.id.loading_layout, View.GONE);
            views.setViewVisibility(R.id.data_layout, View.VISIBLE);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        Log.v(LOG_TAG, "onUpdate finished");
    }

}
