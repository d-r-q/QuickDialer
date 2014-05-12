package ru.jdev.qd.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import ru.jdev.qd.QdWidgetProvider;

public class TapListenerService extends IntentService {

    private static final String TAG = "QD.TL";

    public static final String EXTRA_APP_WIDGET_ID = "appWidgetId";

    public static final String PHONE_TO_CALL = "phoneToCall";

    public TapListenerService() {
        super("TapListenerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final int unknown = -1;
        final int appWidgetId = intent.getIntExtra(EXTRA_APP_WIDGET_ID, unknown);
        if (appWidgetId == unknown) {
            return;
        }

        final String activePhone = QdWidgetProvider.getActivePhone(this, appWidgetId);
        Log.v(TAG, activePhone == null ? "null" : activePhone);

        final String selectedPhone = intent.getStringExtra(PHONE_TO_CALL);
        if (isActivePhoneSelected(activePhone, selectedPhone)) {
            startDial(activePhone);
        } else {
            activatePhone(appWidgetId, selectedPhone);
        }

    }

    private void activatePhone(int appWidgetId, String newPhoneToCall) {
        QdWidgetProvider.setActivePhone(this, appWidgetId, newPhoneToCall);
        startService(new Intent(this, UpdateService.class));
        setupDeactivateNotification(appWidgetId, newPhoneToCall);
    }

    private void startDial(String phoneToDeal) {
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + phoneToDeal));
        startActivity(callIntent);
    }

    private boolean isActivePhoneSelected(String activePhone, String selectedPhone) {
        return activePhone != null && activePhone.equals(selectedPhone);
    }

    private void setupDeactivateNotification(int appWidgetId, String activePhone) {
        final Intent deactivateIntent = DeactivateService.createIntent(this, appWidgetId, activePhone);
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500,
                PendingIntent.getService(this, 0, deactivateIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public static Intent createIntent(Context context, int appWidgetId, String phoneToCall) {
        final Intent handleTapIntent = new Intent(context, TapListenerService.class);
        handleTapIntent.putExtra(TapListenerService.PHONE_TO_CALL, phoneToCall);
        handleTapIntent.putExtra(TapListenerService.EXTRA_APP_WIDGET_ID, appWidgetId);
        return handleTapIntent;
    }
}
