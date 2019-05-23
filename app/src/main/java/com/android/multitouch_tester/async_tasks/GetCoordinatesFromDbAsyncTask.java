package com.android.multitouch_tester.async_tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;

import com.android.multitouch_tester.database.LastTouchesDbContract.LastTouchesEntry;
import com.android.multitouch_tester.auxiliary_classes.Coordinates;
import com.android.multitouch_tester.database.LastTouchesDbHelper;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * This class is used for retrieving the coordinates of all touches since installing the app and their colours from the db
 * on a different thread and invalidating a textView after that.
 * All object fields are with WeakReference to avoid memory leaks and enable garbage collection
 * if the calling activity is destroyed.
 */
public class GetCoordinatesFromDbAsyncTask extends AsyncTask<Void, Void, Void> {
    /** Weak reference to the context for the db. */
    private WeakReference<Context> context;

    /** Weak Reference to the list of coordinates to be filled. */
    private WeakReference<List<Coordinates>> coordinates;

    /** Weak reference to the map with the colours for each coordinates. */
    private WeakReference<Map<Coordinates, Short>> coordinatesColours;

    /** Weak reference to the view to be invalidated. */
    private WeakReference<View> view;

    /** String with the columns for the retreiving query. */
    private static final String[] COLUMNS = {
            LastTouchesEntry.COLUMN_NAME_X_COORDINATE,
            LastTouchesEntry.COLUMN_NAME_Y_COORDINATE,
            LastTouchesEntry.COLUMN_NAME_COLOUR
    };

    /**
     * Parametrised constructor fot the AsyncTast.
     * Initialises the fields.
     * @param view the view to be invalidated
     * @param context the context for teh db connection
     * @param coordinates the list with coordinates to be filled
     * @param coordinatesColours the colours for each of the coordinates
     */
    public GetCoordinatesFromDbAsyncTask(View view, Context context, List<Coordinates> coordinates, Map<Coordinates, Short> coordinatesColours) {
        this.context = new WeakReference<>(context);
        this.coordinates = new WeakReference<>(coordinates);
        this.coordinatesColours = new WeakReference<>(coordinatesColours);
        this.view = new WeakReference<>(view);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Create a readable connection with the database.
        LastTouchesDbHelper helper = new LastTouchesDbHelper(this.context.get());
        SQLiteDatabase db = helper.getReadableDatabase();

        // Create a cursor with the result from a query returning all entries in the table.
        Cursor cursor = db.query(LastTouchesEntry.COORDINATES_TABLE_NAME, COLUMNS, null,
                null, null, null, null);

        // Traversing the cursor and adding the values to the list and the map.
        while(cursor.moveToNext()) {
            float x = cursor.getFloat(cursor.getColumnIndexOrThrow(LastTouchesEntry.COLUMN_NAME_X_COORDINATE)),
                    y = cursor.getFloat(cursor.getColumnIndexOrThrow(LastTouchesEntry.COLUMN_NAME_Y_COORDINATE));
            short colour = cursor.getShort(cursor.getColumnIndexOrThrow(LastTouchesEntry.COLUMN_NAME_COLOUR));
            Coordinates coordinates = new Coordinates(x, y);
            this.coordinates.get().add(coordinates);
            this.coordinatesColours.get().put(coordinates, colour);
        }

        // Closing the cursor and db connection + the helper.
        cursor.close();
        db.close();
        helper.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Invalidating the view.
        this.view.get().invalidate();
    }
}
