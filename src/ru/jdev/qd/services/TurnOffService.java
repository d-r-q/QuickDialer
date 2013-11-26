package ru.jdev.qd.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.R;

/**
 * User: Aleksey Zhidkov
 * Date: 09.10.12
 */
public class TurnOffService extends IntentService {

    private static final String TAG = "QD.TOS";

    public TurnOffService() {
        super(TurnOffService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(TAG, "Turn off");
        final SharedPreferences prefs = getSharedPreferences(QdWidgetProvider.PREFS_FILE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneToCall", null);
        editor.commit();

        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_main);
        final int labelId = intent.getIntExtra("labelId", -1);
        views.setInt(labelId, "setTextColor", getResources().getColor(R.color.nonActiveLabelColor));

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(intent.getIntExtra("appWidgetId", -1), views);
    }

}
