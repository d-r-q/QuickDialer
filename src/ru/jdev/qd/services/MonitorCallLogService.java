package ru.jdev.qd.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.CallLog;

public class MonitorCallLogService extends Service {

    public static final int CALL_LOG_UPDATE_TIMEOUT = 60 * 1000;
    private final HandlerThread handlerThread = new HandlerThread("Model an UI updating thread");

    private Handler handler;
    private CallLogObserver observer;

    @Override
    public void onCreate() {
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        observer = new CallLogObserver(handler, this);
        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, false, observer);
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);
        handlerThread.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
