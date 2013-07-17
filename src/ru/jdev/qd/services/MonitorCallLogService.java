/*
 *
 *  * Copyright (c) 2012 Aleksey Zhidkov. All Rights Reserved.
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

import java.util.concurrent.TimeUnit;
public class MonitorCallLogService extends Service {

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
