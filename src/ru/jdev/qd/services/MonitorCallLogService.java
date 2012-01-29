/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *  
 */

package ru.jdev.qd.services;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.CallLog;
import ru.jdev.qd.tasks.StopServiceTask;

/**
 * User: jdev
 * Date: 28.01.12
 */
public class MonitorCallLogService extends Service {

    public static final String EXTRA_IS_IN_IDLE = "isInIdle";
    private static final int WAIT_FOR_UPDATE_TIMEOUT = 60 * 1000;

    private final HandlerThread handlerThread = new HandlerThread("Model an UI updating thread");

    private Handler handler;
    private ContentObserverImpl observer;

    @Override
    public void onCreate() {
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        observer = new ContentObserverImpl(handler);
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false, observer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(EXTRA_IS_IN_IDLE, true)) {
            handler.postDelayed(new StopServiceTask(this, StopServiceTask.FORCE_STOP), WAIT_FOR_UPDATE_TIMEOUT);
        }
        
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);
        handlerThread.quit();
    }

    private class ContentObserverImpl extends ContentObserver {
        public ContentObserverImpl(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            final Intent updateWidgets = new Intent(getApplicationContext(), UpdateService.class);
            updateWidgets.putExtra(UpdateService.EXTRA_FORCE_UPDATE_DAO, true);
            startService(updateWidgets);
            handler.post(new StopServiceTask(MonitorCallLogService.this, StopServiceTask.FORCE_STOP));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
