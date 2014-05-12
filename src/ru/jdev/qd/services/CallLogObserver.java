package ru.jdev.qd.services;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

class CallLogObserver extends ContentObserver {

    private static final String TAG = "QD.CLO";

    private final Context context;

    public CallLogObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.i(TAG, "CallLogObserver onChange, uri = " + uri);
        final Intent updateIntent = UpdateService.createIntent(context, null, true);
        context.startService(updateIntent);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

}
