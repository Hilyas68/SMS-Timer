/*
 * Copyright (c) 2017. Ibanga Enoobong Ime (World class developer and entrepreneur in transit)
 */

package com.hcodestudio.smstimer.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class TimerContract {
    static final String CONTENT_AUTHORITY = "com.hcodestudio.smstimer";
    static final String BASE_PATH = "schedules";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final Uri TIMER_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(BASE_PATH).build();

    public static Uri buildRecipeUriWithId(int id) {
        return TIMER_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
    }

    public static final class TimerEntry implements BaseColumns {

        public static final String TABLE_NOTES = "schedules";
        public static final String TIMER_ID = "_id";
        public static final String PHONENO = "phoneNo";
        public static final String NAME = "name";
        public static final String MESSAGE = "message";
        public static final String DATE = "date";
        public static final String TIME = "time";


    }
}
