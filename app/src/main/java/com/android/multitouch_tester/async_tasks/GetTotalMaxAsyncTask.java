package com.android.multitouch_tester.async_tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;

import com.android.multitouch_tester.database.LastTouchesDbContract;
import com.android.multitouch_tester.database.LastTouchesDbHelper;

import java.lang.ref.WeakReference;

/**
 * This class is used for getting the total maximum simultaneous touches count from the db on a different thread
 * and changing a textView with the count.
 * All object fields are with WeakReference to avoid memory leaks and enable garbage collection
 * if the calling activity is destroyed.
 */
public class GetTotalMaxAsyncTask extends AsyncTask<Void, Void, Integer> {
    /** The view to be changed. */
    private WeakReference<TextView> textView;

    /** The context for the database. */
    private WeakReference<Context> context;

    /**
     * Parametrised constructor for the AsyncTask.
     * Initialises the fields.
     * @param context the context for the db
     * @param textView the view to be changed
     */
    public GetTotalMaxAsyncTask(Context context, TextView textView){
        this.textView = new WeakReference<>(textView);
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        // Create a readable connection to the db.
        LastTouchesDbHelper helper = new LastTouchesDbHelper(context.get());
        SQLiteDatabase db = helper.getReadableDatabase();

        // Then create a cursor for the query for getting the total max ever achieved since installing the app.
        Cursor cursor = db.query(LastTouchesDbContract.LastTouchesEntry.MAX_TOUCHES_COUNT_TABLE_NAME,
                new String[]{LastTouchesDbContract.LastTouchesEntry.COLUMN_NAME_MAX_TOUCHES},
                null, null, null, null, null);

        // Creating a variable for the max with a default value of 0
        int max = 0;

        // Check whether there is a row for that in the table and if so put it in the max variable.
        if (cursor.moveToNext()){
            max = cursor.getInt(cursor.
                    getColumnIndexOrThrow(LastTouchesDbContract.LastTouchesEntry.COLUMN_NAME_MAX_TOUCHES));
        }

        // Close the cursor and the db connection + the helper.
        cursor.close();
        db.close();
        helper.close();

        return max;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        // Change the view.
        this.textView.get().setText(String.valueOf(integer));
    }
}
