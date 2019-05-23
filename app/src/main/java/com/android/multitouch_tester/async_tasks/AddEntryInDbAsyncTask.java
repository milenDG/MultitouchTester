package com.android.multitouch_tester.async_tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;

import com.android.multitouch_tester.database.LastTouchesDbContract.LastTouchesEntry;
import com.android.multitouch_tester.database.LastTouchesDbHelper;

import java.lang.ref.WeakReference;

/**
 * Adding a new entry of coordinates to the database and update a text view with the total count.
 * All object fields are with WeakReference to avoid memory leaks and enable garbage collection
 * if the calling activity is destroyed.
 */
public class AddEntryInDbAsyncTask extends AsyncTask<Void, Void, Long> {
    /** Weak reference with the context for the db. */
    private WeakReference<Context> context;

    /** The x and y coordinates of the new entry to the db. */
    private float xCoordinate;
    private float yCoordinate;

    /** The colour of the new coordinates. */
    private short colour;

    /** Weak reference to the textView to be updated on the end aft the Thread. */
    private WeakReference<TextView> textView;

    /**
     * Parametrised constructor for the async task.
     * Initialises the fields.
     * @param context context for the db
     * @param xCoordinate x-coordinate
     * @param yCoordinate y-coordinate
     * @param colour colour of the new entry
     * @param textView The text view to be updated.
     */
    public AddEntryInDbAsyncTask(Context context, float xCoordinate, float yCoordinate, short colour, TextView textView){
        this.context = new WeakReference<>(context);
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.colour = colour;
        this.textView = new WeakReference<>(textView);
    }

    @Override
    protected Long doInBackground(Void... voids) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LastTouchesEntry.COLUMN_NAME_X_COORDINATE, this.xCoordinate);
        values.put(LastTouchesEntry.COLUMN_NAME_Y_COORDINATE, this.yCoordinate);
        values.put(LastTouchesEntry.COLUMN_NAME_COLOUR, this.colour);

        // Create a new writable connection to the database and insert the new row.
        LastTouchesDbHelper helper = new LastTouchesDbHelper(this.context.get());
        SQLiteDatabase db = helper.getWritableDatabase();

        // Then return the row number of the added row.
        long rows = db.insert(LastTouchesEntry.COORDINATES_TABLE_NAME, null, values);

        // Close db connection and the helper.
        db.close();
        helper.close();

        return rows;
    }

    @Override
    protected void onPostExecute(Long rowsCount) {
        super.onPostExecute(rowsCount);

        // Check whether the instance was inserted and if so chane the view.
        if (rowsCount > 0){
            this.textView.get().setText(String.valueOf(rowsCount));
        }
    }
}
