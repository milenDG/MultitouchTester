package com.android.multitouch_tester.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.multitouch_tester.R;
import com.android.multitouch_tester.async_tasks.GetTotalTouchesAsyncTask;

/**
 * This class represents the second activity in the application.
 * It is used to test the multi-touch of a device.
 */
public class MultiTouchActivity extends Activity {
    /** Maximum achieved simultaneous touches in the current activity. */
    private TextView maximumTouches;

    /** The total touches since the installing the app. */
    private TextView totalCount;

    /** Used for saving the state of the instance whn destroying it. */
    private static String CURRENT_MAX_KEY = "current_max";
    private static String CURRENT_TOTAL_TOUCHES = "total_touches";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_touch);

        // Finding the views from the layout.
        this.maximumTouches = findViewById(R.id.maxCountTextViewLastTouches);
        this.totalCount = findViewById(R.id.totalMaxTextViewLastTouches);

        // If the last instance is saved then restore it.
        // Else set the max to 0 and the total touches from the db on a different thread.
        if (savedInstanceState != null) {
            this.maximumTouches.append(String.valueOf(savedInstanceState.getInt(CURRENT_MAX_KEY)));
            this.totalCount.setText(savedInstanceState.getString(CURRENT_TOTAL_TOUCHES));
        } else {
            this.maximumTouches.setText("0");
            new GetTotalTouchesAsyncTask(getApplicationContext(), this.totalCount).execute();
        }
    }

    /**
     * Change the maximum registered simultaneous touches in the current activity.
     * @param newMax new maximum
     */
    public void changeMax(int newMax) {
        this.maximumTouches.setText(String.valueOf(newMax));
    }

    /**
     * Get the current maximum achieved in this instance of the activity.
     * @return current maximum
     */
    public int getCurrentMax() {
        int max = 0;
        try{
            max = Integer.parseInt(this.maximumTouches.getText().toString());
        } catch (Exception e) {
            // do nothing there is a default value for max.
        }

        return max;
    }

    /**
     * Save the data in the current instance before destroying it.
     * @param outState the state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_MAX_KEY, this.maximumTouches.getText().toString());
        outState.putString(CURRENT_TOTAL_TOUCHES, this.totalCount.getText().toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * Get the total touches count since installing the application.
     * @return total count
     */
    public TextView getTotalCount() {
        return this.totalCount;
    }

    /**
     * If the back button is pressed, return the result to MainActivity.
     */
    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra(MainActivity.MULTI_TOUCH_ACTIVITY_RESULT, Integer.parseInt(this.maximumTouches.getText().toString()));
        setResult(RESULT_OK, output);
        finish();
        super.onBackPressed();
    }

}