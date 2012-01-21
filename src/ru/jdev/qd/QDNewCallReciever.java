/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *
 */

package ru.jdev.qd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.LinkedList;

public class QDNewCallReciever extends BroadcastReceiver {

    private static final String TAG = "QD.NCR";

    public void onReceive(Context context, Intent intent) {
        final String number = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        getContactNameFromNumber(context, number);
    }

    private String getContactNameFromNumber(Context context, String number) {

        Log.v(TAG, "Querying data");
        final Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        final Cursor c = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.LOOKUP_KEY}, null, null, null);
        try {
            if (c.moveToFirst()) {
                final LinkedList<String> contactIds = new LinkedList<String>();
                do {
                    contactIds.add(c.getString(0));
                } while (c.moveToNext());

                if (contactIds.size() == 0) {
                    return contactIds.getFirst();
                }

            } else {
                Log.v(TAG, "No Data available");
                return null;
            }
        } finally {
            c.close();
        }

        // return the original number if no match was found
        return number;
    }

}
