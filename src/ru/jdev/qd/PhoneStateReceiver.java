package ru.jdev.qd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ru.jdev.qd.services.MonitorCallLogService;

public class PhoneStateReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MonitorCallLogService.class));
    }

}
