package com.android.multitouch_tester.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.android.multitouch_tester.async_tasks.GetCoordinatesFromDbAsyncTask;
import com.android.multitouch_tester.auxiliary_classes.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will draw all the touches made in the MultiTouchActivity for test since installing the app.
 * It will represent a view integrated in the LastTouchesActivity.
 */
public class LastTouchesView extends View {
    /** List of all coordinates for the circles and lines to be drawn. */
    private List<Coordinates> coordinates;

    /** A map storing the colours of the circles and lines. */
    private Map<Coordinates, Short> coordinatesColours;

    /** The paint for the draws. */
    private Paint paint;

    /**
     * Constructor for the view. Initialises the fields.
     * @param context the context of the view
     * @param attrs additional attributes
     */
    public LastTouchesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.coordinates = new ArrayList<>();
        this.coordinatesColours = new HashMap<>();
        this.paint = new Paint();

        // Start a new thread, which will make a connection to the database and get all the needed coordinates and their colours.
        new GetCoordinatesFromDbAsyncTask(this, getContext(),
                this.coordinates, this.coordinatesColours).execute();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Paint the canvas in black.
        canvas.drawColor(Color.BLACK);

        // Call the static method in MultiTouchTestView doing the same work. (code reusability)
        MultiTouchTestView.drawTouches(canvas, this.coordinates, this.paint, this.coordinatesColours);
    }
}
