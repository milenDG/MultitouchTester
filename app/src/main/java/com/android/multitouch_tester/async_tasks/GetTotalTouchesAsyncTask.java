package com.android.multitouch_tester.async_tasks;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;

import com.android.multitouch_tester.database.LastTouchesDbContract;
import com.android.multitouch_tester.database.LastTouchesDbHelper;

import java.lang.ref.WeakReference;

/**
 * This class is used for getting the total touches count from the db on a different thread
 * and changing a textView with the count.
 * All object fields are with WeakReference to avoid memory leaks and enable garbage collection
 * if the calling activity is destroyed.
 */
public class GetTotalTouchesAsyncTask extends AsyncTask<Void, Void, Long> {
    /** Context for creating the db reference. */
    private WeakReference<Context> context;

    /** The text view to be changed */
    private WeakReference<TextView> textView;

    /**
     * Parametrised constructor for the AsyncTask.
     * Initialises the fields.
     * @param context the context for the db
     * @param textView the text view to be changed
     */
    public GetTotalTouchesAsyncTask(Context context, TextView textView) {
        this.context = new WeakReference<>(context);
        this.textView = new WeakReference<>(textView);
    }

    @Override
    protected Long doInBackground(Void... voids) {
        // Create the db connection.
        LastTouchesDbHelper helper = new LastTouchesDbHelper(this.context.get());
        SQLiteDatabase db = helper.getReadableDatabase();

        // Return the rows count using DatabaseUtils class.
        long count = DatabaseUtils
                .queryNumEntries(db,LastTouchesDbContract.LastTouchesEntry
                                .COORDINATES_TABLE_NAME);

        // Close the helper and the db connection.
        db.close();
        helper.close();

        return count;
    }

    @Override
    protected void onPostExecute(Long result) {
        super.onPostExecute(result);

        // Change the view.
        this.textView.get()
                .setText(String.valueOf(result));
    }
}
