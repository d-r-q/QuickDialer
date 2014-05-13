package ru.jdev.qd.model;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.*;

public class ContactInfoDao {

    private static final String TAG = "QD.CID";

    private static final String SELECT_CALLS_WHERE = String.format("%s >= ?", CallLog.Calls.DATE);

    private static final String[] callsProjection = new String[]{
            CallLog.Calls.NUMBER, CallLog.Calls.DATE
    };
    private static final String[] contactsProjection = new String[]{
            ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.LOOKUP_KEY, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_ID
    };
    private static final int NO_PHOTO = -999;
    private static final String NO_LOOKUP_KEY = "QD.No lookup key";

    private final Map<String, List<ContactInfo>> contactsByPhones = new HashMap<String, List<ContactInfo>>();
    private final Map<String, ContactInfo> contactsByKeys = new HashMap<String, ContactInfo>();

    private final Context context;

    private long lastUpdateTimeMillis = 0;

    public ContactInfoDao(Context context) {
        this.context = context;
    }

    public synchronized boolean update() {
        Log.i(TAG, "Update ContactInfoDao");
        final String[] params = {Long.toString(lastUpdateTimeMillis)};
        final Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, callsProjection, SELECT_CALLS_WHERE, params, null);
        boolean isUpdated = false;
        try {
            if (c != null && c.moveToFirst()) {
                do {
                    final String calledPhone = c.getString(0);
                    final long callDate = Long.valueOf(c.getString(1));
                    if (calledPhone != null && calledPhone.trim().length() > 0) {
                        addCall(calledPhone, callDate);
                    }
                    isUpdated = true;
                } while (c.moveToNext());
            }
            if (isUpdated) {
                lastUpdateTimeMillis = System.currentTimeMillis();
            }
            return isUpdated;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public synchronized void updatePhotos() {
        for (ContactInfo contactInfo : contactsByKeys.values()) {
            if (contactInfo.getName() == null || contactInfo.getName().startsWith(NO_LOOKUP_KEY)) {
                continue;
            }
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactInfo.getLookupId());
            final Uri uri = ContactsContract.Contacts.lookupContact(context.getContentResolver(), lookupUri);
            final Cursor c = context.getContentResolver().query(uri, new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.PHOTO_ID}, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    boolean hasPhoto = !c.isNull(1);
                    final long id = c.getLong(0);
                    long personId = hasPhoto ? id : NO_PHOTO;
                    contactInfo.setPersonUri(getPersonUri(personId));
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }

    private void addCall(String calledPhone, long callDate) {
        final List<ContactInfo> contactInfos = getContactInfos(calledPhone, context);
        for (ContactInfo ci : contactInfos) {
            if (ci.lastCall < callDate) {
                ci.lastCall = callDate;
                ci.lastDialedPhone = calledPhone;
            }
            ci.usage++;
        }
    }

    private List<ContactInfo> getContactInfos(String phone, Context context) {
        List<ContactInfo> contactInfos = contactsByPhones.get(phone);

        if (contactInfos == null) {
            contactInfos = new LinkedList<ContactInfo>();

            final Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
            final Cursor c = context.getContentResolver().query(uri, contactsProjection, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    do {
                        final long id = c.getLong(0);
                        final String lookupKey = c.getString(1);
                        final String displayName = c.getString(2);
                        final boolean hasPhoto = !c.isNull(3);

                        contactInfos.add(getContactInfo(lookupKey, phone, displayName, hasPhoto ? id : NO_PHOTO));
                    } while (c.moveToNext());
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            if (contactInfos.size() == 0) {
                contactInfos.add(getContactInfo(NO_LOOKUP_KEY + phone, phone, null, NO_PHOTO));
            }

            contactsByPhones.put(phone, contactInfos);
        }

        return contactInfos;
    }

    private ContactInfo getContactInfo(String lookupKey, String phone, String name, long personId) {
        ContactInfo ci = contactsByKeys.get(lookupKey);
        if (ci == null) {
            final Uri personUri = getPersonUri(personId);

            ci = new ContactInfo(name, phone, lookupKey, personUri);
            synchronized (contactsByKeys) {
                contactsByKeys.put(lookupKey, ci);
            }
        }
        return ci;
    }

    private Uri getPersonUri(long personId) {
        return personId != NO_PHOTO ?
                ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI, personId) :
                null;
    }

    public Collection<ContactInfo> getContactInfoList() {
        synchronized (contactsByKeys) {
            return new LinkedList<ContactInfo>(contactsByKeys.values());
        }
    }

    public boolean isUpdated() {
        return lastUpdateTimeMillis > 0 && contactsByKeys.size() > 0;
    }

}
