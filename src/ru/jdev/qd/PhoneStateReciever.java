/*
 *
 *  * Copyright (c) 2012 Aleksey Zhidkov. All Rights Reserved.
 *
 */

package ru.jdev.qd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import ru.jdev.qd.services.MonitorCallLogService;

public class PhoneStateReciever extends BroadcastReceiver {

    private static final String TAG = "QD.PSR";

    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, intent.toString());
        Log.v(TAG, intent.getStringExtra(TelephonyManager.EXTRA_STATE));
        final Intent monitorCallLog = new Intent(context, MonitorCallLogService.class);
        monitorCallLog.putExtra(MonitorCallLogService.EXTRA_IS_IN_IDLE,
                TelephonyManager.EXTRA_STATE_IDLE.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE)));
        context.startService(new Intent(context, MonitorCallLogService.class));
    }

}
