package ru.jdev.qd.services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import ru.jdev.qd.QdWidgetProvider;
import ru.jdev.qd.model.ContactInfoDao;
import ru.jdev.qd.model.Pager;
import ru.jdev.qd.tasks.ContactImageFactory;
import ru.jdev.qd.tasks.UpdatePagerTask;
import ru.jdev.qd.tasks.UpdateWidgetsTask;
import ru.jdev.qd.utils.StopServiceTask;

public class UpdateService extends Service {

    private static final String TAG = "QD.US";

    private static final long KEEP_IN_MEMORY_TIME = 1000 * 60;

    public static final String EXTRA_APP_WIDGET_IDS = "appWidgetIds";
    public static final String EXTRA_FORCE_UPDATE_DAO = "forceUpdateDao";

    private static ContactInfoDao contactInfoDao;
    private static Pager pager;
    private static ContactImageFactory contactImageFactory;

    private final HandlerThread handlerThread = new HandlerThread("Model an UI updating thread");

    private Handler handler;

    @Override
    public void onCreate() {
        Log.v(TAG, "UpdateService created");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
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

        // keep app in memory for minute, for quick response for consequent calls
        handler.postDelayed(new StopServiceTask(this, startId), KEEP_IN_MEMORY_TIME);
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
            contactImageFactory = new ContactImageFactory();
        }
        return contactInfoDao;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "UpdateService destroyed");
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
