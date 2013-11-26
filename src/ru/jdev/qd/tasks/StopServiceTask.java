package ru.jdev.qd.tasks;

import android.app.Service;
import ru.jdev.qd.services.UpdateService;

/**
 * User: jdev
 * Date: 28.01.12
 */
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
