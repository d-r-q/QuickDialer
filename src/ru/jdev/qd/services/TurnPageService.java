/*
 *
 *  * Copyright (c) 2012 Aleksey Zhidkov. All Rights Reserved.
 *  
 */

package ru.jdev.qd.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.R;
import ru.jdev.qd.Utils;

public abstract class TurnPageService extends IntentService {

    private static final String TAG = "QD.TPS";

    public static final String EXTRA_APP_WIDGET_ID = "appWidgetId";
    
    private static final int NO_WIDGET_ID = -999;

    public TurnPageService() {
        super("TurnPageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Intent received: " + intent.toString());
        final int appWidgetId = intent.getIntExtra(EXTRA_APP_WIDGET_ID, NO_WIDGET_ID);
        if (appWidgetId == NO_WIDGET_ID) {
            Log.w(TAG, "Invalid intent received: " + intent.toString());
            return;
        }
        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_main);
        views.setViewVisibility(R.id.darkener, View.VISIBLE);
        AppWidgetManager.getInstance(this).updateAppWidget(appWidgetId, views);

        Log.v(TAG, intent.getExtras().toString());

        final SharedPreferences prefs = getSharedPreferences(QdWidgetProvider.PREFS_FILE_NAME, MODE_PRIVATE);
        final String widgetPagePropName = Utils.getWidgetPageProperty(appWidgetId);
        final int pagesCount = Utils.getPagesCount();

        final int curPage = turnPage(prefs.getInt(widgetPagePropName, 0), pagesCount);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(widgetPagePropName, curPage);
        editor.commit();

        final Intent updateWidgets = new Intent(this, UpdateService.class);
        updateWidgets.putExtra(UpdateService.EXTRA_APP_WIDGET_IDS, new int[]{appWidgetId});
        startService(updateWidgets);
    }

    protected abstract int turnPage(int curPage, int pagesCount);
}
