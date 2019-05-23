package com.android.multitouch_tester.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.multitouch_tester.R;
import com.android.multitouch_tester.async_tasks.GetTotalMaxAsyncTask;

/**
 * The main activity in the application.
 * Used for choosing what functionality from the app to use and
 * familiarising the user with the purpose of the app.
 */
public class MainActivity extends AppCompatActivity {
    /**  Used to determine the request and result when starting the MultiTouchActivity for result.*/
    public static final String MULTI_TOUCH_ACTIVITY_RESULT = "multi-touch_result";
    private static final int RESULT_REQUEST = 1;

    /** Used for keys in the bundle when performing onSaveInstanceState. */
    private static final String CURRENT_MAX_KEY = "current_max";
    private static final String TOTAL_MAX_KEY = "total_max";

    /** The current maximum of max simultaneous touches. */
    private TextView currentMax;

    /** The total maximum ever achieved for multi-touch since installing the app. */
    private TextView totalMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Finding the views from the layout.
        this.currentMax = findViewById(R.id.currentMaxTextView);
        this.totalMax = findViewById(R.id.totalMaxTextView);

        // If the last instance was saved then just restore the textViews.
        // Else set the initial value for current and start new thread to get from the db the total max ever.
        if (savedInstanceState != null) {
            this.currentMax.setText(savedInstanceState.getString(CURRENT_MAX_KEY));
            this.totalMax.setText(savedInstanceState.getString(TOTAL_MAX_KEY));
        } else {
            this.currentMax.setText("0");
            new GetTotalMaxAsyncTask(getApplicationContext(), this.totalMax).execute();
        }
    }

    /**
     * Starting the MultiTouchActivity for result; used by the start test button.
     * @param view the button clicked.
     */
    public void startMultiTouch(View view) {
        Intent intent = new Intent(this, MultiTouchActivity.class);
        startActivityForResult(intent, RESULT_REQUEST);
    }

    /**
     * Starting the LastTouchesActivity
     * @param view the button clicked
     */
    public void startDrawOfLastTouches(View view) {
        Intent intent = new Intent(this, LastTouchesActivity.class);
        startActivity(intent);
    }

    /**
     * Respond to the result from starting MultiTouchActivity.
     * @param requestCode request code if successful
     * @param resultCode result code if successful
     * @param data the data in the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Request to respond to.
        if (requestCode == RESULT_REQUEST) {
            // Was successful.
            if (resultCode == RESULT_OK) {
                // Get current maximum and set it in the view.
                int currentMaxInt = data.getIntExtra(MULTI_TOUCH_ACTIVITY_RESULT, 0);
                currentMax.setText(String.valueOf(currentMaxInt));

                // If the current max is greater than the total max change it.
                if (currentMaxInt > Integer.parseInt(this.totalMax.getText().toString())) {
                    this.totalMax.setText(String.valueOf(currentMaxInt));
                }
            }
        }
    }

    /**
     * Save the last instance of the view.
     * @param outState the saved state data
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_MAX_KEY, this.currentMax.getText().toString());
        outState.putString(TOTAL_MAX_KEY, this.totalMax.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
