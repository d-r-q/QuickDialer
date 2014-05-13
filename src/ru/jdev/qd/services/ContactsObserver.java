package ru.jdev.qd.services;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import ru.jdev.qd.tasks.ContactImageFactory;

public class ContactsObserver extends ContentObserver {

    private static final String TAG = "QD.CO";

    private final ContactImageFactory contactImageFactory;
    private final Context context;

    public ContactsObserver(Handler handler, Context context, ContactImageFactory contactImageFactory) {
        super(handler);
        this.context = context;
        this.contactImageFactory = contactImageFactory;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.i(TAG, "ContactsObserver onChange, uri = " + uri);
        contactImageFactory.evictContactPhotos();

        final Intent updateIntent = UpdateService.createIntent(context, null, true);
        context.startService(updateIntent);
    }

}
