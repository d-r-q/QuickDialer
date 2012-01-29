/*
 *
 *  * Copyright (c) 2012 Alexey Zhidkov (Jdev). All Rights Reserved.
 *
 */

package ru.jdev.qd.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;

public class QdSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "QD.SQL";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "QuickDialer";

    private static final String CALL_LOG_TBL = "qd_call_log";

    private static final String TIME_COL = "time";
    private static final String PHONE_COL = "lastDialedPhone";
    private static final String CONTACT_ID_COL = "contact_id";
    private static final String TIME_OF_DAY_COL = "time_of_day";

    private static final String CALL_LOG_TABLE_CREATE =
            String.format("CREATE TABLE %s (%s INTEGER, %s TEXT, %s INTEGER, %s INTEGER);",
                    CALL_LOG_TBL, TIME_COL, PHONE_COL, CONTACT_ID_COL, TIME_OF_DAY_COL);

    private static final String INSERT_LOG_ENTRY = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
            CALL_LOG_TBL, TIME_COL, PHONE_COL, CONTACT_ID_COL, TIME_OF_DAY_COL);

    private final SQLiteDatabase db;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public QdSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CALL_LOG_TABLE_CREATE);
    }

    public void insertLogEntry(String phone, String contactId) {        
        final long time = System.currentTimeMillis();
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        final int timeOfDay = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

        final String[] queryParams = {Long.toString(time), phone, contactId, Integer.toString(timeOfDay)};
        try {
            db.execSQL(INSERT_LOG_ENTRY, queryParams);
        } catch (SQLException e) {
            Log.e(LOG_TAG, String.format("Log entry insertion error.\nQuery: %s (%s)", INSERT_LOG_ENTRY, Arrays.toString(queryParams)), e);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
