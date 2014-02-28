package ru.jdev.qd;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import ru.jdev.qd.services.UpdateService;

public class QdWidgetProvider extends AppWidgetProvider {

    public static final String PREFS_FILE_NAME_PREFIX = "qd_prefs";

    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(UpdateService.createIntent(context, appWidgetIds, true));
    }

    public static String getActivePhone(Context context, int appWidgetId) {
        final SharedPreferences prefs = getSharedPreferences(context, appWidgetId);
        return prefs.getString("phoneToCall", null);
    }

    public static void setActivePhone(Context context, int appWidgetId, String activePhone) {
        final SharedPreferences prefs = getSharedPreferences(context, appWidgetId);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneToCall", activePhone);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context, int appWidgetId) {
        return context.getSharedPreferences(QdWidgetProvider.PREFS_FILE_NAME_PREFIX + appWidgetId, Context.MODE_PRIVATE);
    }

}
