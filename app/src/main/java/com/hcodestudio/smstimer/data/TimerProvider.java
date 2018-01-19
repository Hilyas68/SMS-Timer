package com.hcodestudio.smstimer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.hcodestudio.smstimer.data.TimerContract.BASE_PATH;
import static com.hcodestudio.smstimer.data.TimerContract.CONTENT_AUTHORITY;
import static com.hcodestudio.smstimer.data.TimerContract.TIMER_CONTENT_URI;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TABLE_NOTES;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TIMER_ID;

/**
 * Created by hassan on 8/14/2017.
 */

public class TimerProvider extends ContentProvider {

    // Constant to identify the requested operation
    private static final int SCHEDULE = 1;
    private static final int SCHEDULE_ID = 2;

    public static final String CONTENT_ITEM_TYPE = "Schedule";

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public SQLiteDatabase database;
    private TimerDBOpenHelper helper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, BASE_PATH, SCHEDULE);
        uriMatcher.addURI(CONTENT_AUTHORITY, BASE_PATH + "/#", SCHEDULE_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
         helper = new TimerDBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    int mRowsUpdated;

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        final SQLiteDatabase db = helper.getReadableDatabase();

        switch (sUriMatcher.match(uri)) {
            case SCHEDULE_ID:
                cursor = db.query(TABLE_NOTES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SCHEDULE:
                cursor = db.query(TABLE_NOTES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                         TIMER_ID + " DESC");
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (sUriMatcher.match(uri)) {
            case SCHEDULE_ID:
                id = db.insert(TABLE_NOTES, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TIMER_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case SCHEDULE:
                id = db.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TIMER_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // final SQLiteDatabase db = helper.getWritableDatabase();

        // return db.delete(TABLE_NOTES, selection, selectionArgs);

        final SQLiteDatabase db = helper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted = 0; // starts as 0

        // COMPLETED (2) Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {

            case SCHEDULE:
                tasksDeleted = db.delete(TABLE_NOTES, selection, selectionArgs);
                break;
            // Handle the single item case, recognized by the ID included in the URI path
            case SCHEDULE_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TABLE_NOTES, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return tasksDeleted;
    }

//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        return database.delete(TABLE_NOTES, selection, selectionArgs);
//    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(TABLE_NOTES,
                contentValues, selection, selectionArgs);
    }

}
