package ru.jdev.qd.services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.model.ContactInfoDao;
import ru.jdev.qd.model.Pager;
import ru.jdev.qd.tasks.ContactImageFactory;
import ru.jdev.qd.tasks.UpdatePagerTask;
import ru.jdev.qd.tasks.UpdateWidgetsTask;

public class UpdateService extends Service {

    private static final String TAG = "QD.US";

    public static final String EXTRA_APP_WIDGET_IDS = "appWidgetIds";
    public static final String EXTRA_FORCE_UPDATE_DAO = "forceUpdateDao";

    private static ContactInfoDao contactInfoDao;
    private static Pager pager;
    private static ContactImageFactory contactImageFactory = new ContactImageFactory();

    private final HandlerThread handlerThread = new HandlerThread("Model an UI updating thread");

    private CallLogObserver callLogObserver;
    private ContactsObserver contactsObserver;

    private Handler handler;

    @Override
    public void onCreate() {
        Log.i(TAG, "UpdateService created");

        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        callLogObserver = new CallLogObserver(handler, this);
        contactsObserver = new ContactsObserver(handler, this, contactImageFactory);
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, callLogObserver);
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactsObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final ContactInfoDao contactInfoDao = getContactInfoDao(this);
        Log.v(TAG, "Pager: " + pager);
        Log.v(TAG, "ContactDao is updated: " + contactInfoDao.isUpdated());
        if (!contactInfoDao.isUpdated() || intent.getBooleanExtra(EXTRA_FORCE_UPDATE_DAO, false)) {
            handler.post(new UpdatePagerTask(contactInfoDao, pager));
        }
        handler.post(new UpdateWidgetsTask(contactImageFactory, getApplicationContext(), pager, getAppWidgetIds(intent)));

        return START_REDELIVER_INTENT;
    }

    private int[] getAppWidgetIds(Intent intent) {
        int[] appWidgetIds = intent.getIntArrayExtra(EXTRA_APP_WIDGET_IDS);
        if (appWidgetIds == null) {
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            if (appWidgetManager != null) {
                appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, QdWidgetProvider.class));
            } else {
                appWidgetIds = new int[0];
            }
        }
        return appWidgetIds;
    }

    public static ContactInfoDao getContactInfoDao(Context context) {
        if (contactInfoDao == null) {
            contactInfoDao = new ContactInfoDao(context);
            pager = new Pager(contactInfoDao);
        }
        return contactInfoDao;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "UpdateService destroyed");
        getContentResolver().unregisterContentObserver(callLogObserver);
        getContentResolver().unregisterContentObserver(contactsObserver);
        handlerThread.quit();
    }

    public static Intent createIntent(Context context, int[] appWidgetIds, boolean forceUpdateDao) {
        final Intent updateIntent = new Intent(context, UpdateService.class);

        updateIntent.putExtra(EXTRA_APP_WIDGET_IDS, appWidgetIds);
        updateIntent.putExtra(EXTRA_FORCE_UPDATE_DAO, forceUpdateDao);

        return updateIntent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
