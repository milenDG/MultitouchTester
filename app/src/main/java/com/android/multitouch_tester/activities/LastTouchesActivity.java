package com.android.multitouch_tester.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.multitouch_tester.R;
import com.android.multitouch_tester.async_tasks.GetTotalTouchesAsyncTask;

import java.util.concurrent.Semaphore;

/**
 * This activity will draw all the recorded touches in the MultiTouchActivity.
 * Additionally, it will show other information about the total touches.
 */
public class LastTouchesActivity extends AppCompatActivity {
    /** Used for saving when in onSaveInstanceState. */
    private final static String TOTAL_TOUCHES = "total_touches";

    /** The totalTouches since the installing of the app in the MultiTouchActivity. */
    private TextView totalTouches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_touches);

        // Get the textView.
        this.totalTouches = findViewById(R.id.totalMaxTextViewLastTouches);

        // If it has been saved then there is no need for using the db.
        // Else take the value of the total touches from the db on a new Thread.
        if (savedInstanceState != null) {
            this.totalTouches.setText(savedInstanceState.getString(TOTAL_TOUCHES));
        } else {
            new GetTotalTouchesAsyncTask(getApplicationContext(), this.totalTouches).execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the value of the view.
        outState.putString(TOTAL_TOUCHES, this.totalTouches.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
