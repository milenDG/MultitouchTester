package com.android.multitouch_tester.async_tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.android.multitouch_tester.database.LastTouchesDbContract.LastTouchesEntry;
import com.android.multitouch_tester.database.LastTouchesDbHelper;

import java.lang.ref.WeakReference;

/**
 * This class is used for changing the total maximum simultaneous touches count ever achieved
 * since installing the app from the db on a different thread.
 * All object fields are with WeakReference to avoid memory leaks and enable garbage collection
 * if the calling activity is destroyed.
 */
public class ChangeMaximumTouchesCountAsyncTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<Context> context;

    /** The new maximum count */
    private int count;

    /**
     * Parametrised constructor for the AsyncTask.
     * Initialises the fields.
     * @param context the context for the database connection.
     * @param count the new max count
     */
    public ChangeMaximumTouchesCountAsyncTask(Context context, int count) {
        this.context = new WeakReference<>(context);
        this.count = count;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        LastTouchesDbHelper helper = new LastTouchesDbHelper(context.get());
        SQLiteDatabase db = helper.getReadableDatabase();

        // Create a cursor with the needed entry.
        Cursor cursor = db.query(LastTouchesEntry.MAX_TOUCHES_COUNT_TABLE_NAME,
                new String[]{LastTouchesEntry.COLUMN_NAME_MAX_TOUCHES},
                null, null, null, null, null);
        // Create an integer which will hold the max value with a default value of 0.
        int currentMax = 0;

        // Use the cursor with an if not while, because the table has only one entry.
        if (cursor.moveToNext()) {
            currentMax = cursor.getInt(cursor.
                    getColumnIndexOrThrow(LastTouchesEntry.COLUMN_NAME_MAX_TOUCHES));
        }

        // Close the cursor.
        cursor.close();

        // If the new max is greater than the current, change the entry in the db.
        if (currentMax < this.count) {

            ContentValues values = new ContentValues();
            values.put(LastTouchesEntry.COLUMN_NAME_MAX_TOUCHES, this.count);

            // Delete the last max.
            db.delete(LastTouchesEntry.MAX_TOUCHES_COUNT_TABLE_NAME, "", null);

            // Insert the new max.
            db.insert(LastTouchesEntry.MAX_TOUCHES_COUNT_TABLE_NAME, null, values);
        }

        // Close the connection with the db and the helper.
        db.close();
        helper.close();

        return null;
    }
}