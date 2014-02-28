package ru.jdev.qd.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.jdev.qd.QdWidgetProvider;

public class DeactivateService extends IntentService {

    public static final String APP_WIDGET_ID = "appWidgetId";
    public static final String EXPECTED_PHONE = "expectedPhone";

    private static final String TAG = "QD.TOS";

    public DeactivateService() {
        super(DeactivateService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w(TAG, "Turn off");

        final int unknown = -1;

        final int appWidgetId = intent.getIntExtra(APP_WIDGET_ID, unknown);

        final String expectedActivePhone = intent.getStringExtra(EXPECTED_PHONE);
        final String activatedPhone = QdWidgetProvider.getActivePhone(this, appWidgetId);

        if (appWidgetId != unknown && expectedActivePhone != null && expectedActivePhone.equals(activatedPhone)) {
            QdWidgetProvider.setActivePhone(this, appWidgetId, null);
            startService(new Intent(this, UpdateService.class));
        }
    }

    public static Intent createIntent(Context context, int appWidgetId, String activatePhone) {
        final Intent deactivateIntent = new Intent(context, DeactivateService.class);
        deactivateIntent.putExtra(DeactivateService.APP_WIDGET_ID, appWidgetId);
        deactivateIntent.putExtra(DeactivateService.EXPECTED_PHONE, activatePhone);
        return deactivateIntent;
    }

}
