/*
 *
 *  * Copyright (c) 2012 Aleksey Zhidkov. All Rights Reserved.
 *
 */

package ru.jdev.qd.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.R;

public class TapListenerService extends IntentService {

    private static final String TAG = "QD.TL";

    public static final String EXTRA_APP_WIDGET_ID = "appWidgetId";

    private static final int NO_WIDGET_ID = -999;

    public TapListenerService() {
        super("TapListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final int appWidgetId = intent.getIntExtra(EXTRA_APP_WIDGET_ID, NO_WIDGET_ID);
        if (appWidgetId == NO_WIDGET_ID) {
            return;
        }

        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_main);
        final String phoneToCall = getPhoneToCall();
        Log.i(TAG, phoneToCall == null ? "null" : phoneToCall);
        final int labelId = intent.getIntExtra("labelId", -1);
        if (phoneToCall == null) {
            setPhoneToCall(intent.getStringExtra("phoneToCall"));
            Log.i(TAG, intent.getStringExtra("phoneToCall"));
            views.setFloat(labelId, "setTextSize", 14);
            views.setInt(labelId, "setTextColor", getResources().getColor(R.color.activeLabelColor));
        } else {
            final Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            callIntent.setData(Uri.parse("tel:" + phoneToCall));
            startActivity(callIntent);
        }

        final Intent turnOffIntent = new Intent(this, TurnOffService.class);
        turnOffIntent.putExtra("labelId", labelId);
        turnOffIntent.putExtra("appWidgetId", appWidgetId);
        ((AlarmManager)getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500,
                PendingIntent.getService(this, 0, turnOffIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setPhoneToCall(String phoneToCall) {
        final SharedPreferences prefs = getSharedPreferences(QdWidgetProvider.PREFS_FILE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneToCall", phoneToCall);
        editor.commit();
    }

    public String getPhoneToCall() {
        final SharedPreferences prefs = getSharedPreferences(QdWidgetProvider.PREFS_FILE_NAME, MODE_PRIVATE);
        return prefs.getString("phoneToCall", null);
    }
}
