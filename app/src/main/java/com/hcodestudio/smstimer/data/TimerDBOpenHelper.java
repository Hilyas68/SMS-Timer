package com.hcodestudio.smstimer.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.DATE;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.MESSAGE;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.NAME;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.PHONENO;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TABLE_NOTES;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TIME;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TIMER_ID;

/**
 * Created by hassan on 8/14/2017.
 */

public class TimerDBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "schedules.db";
    private static final int DATABASE_VERSION = 1;

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    TIMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PHONENO + " TEXT, " +
                    NAME + " TEXT DEFAULT 'unnamed', " +
                    MESSAGE + " TEXT, " +
                    DATE + " TEXT, " +
                    TIME + " TEXT " +
                    ")";

    public TimerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(sqLiteDatabase);
    }

}


