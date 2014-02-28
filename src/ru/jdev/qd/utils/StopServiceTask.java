package ru.jdev.qd.utils;

import android.app.Service;

public class StopServiceTask implements Runnable {

    public static final int FORCE_STOP = -999;

    private final Service service;
    private final int startId;

    public StopServiceTask(Service service, int startId) {
        this.service = service;
        this.startId = startId;
    }

    @Override
    public void run() {
        if (startId != FORCE_STOP) {
            service.stopSelfResult(startId);
        } else {
            service.stopSelf();
        }
    }
}
