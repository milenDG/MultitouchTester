package com.android.multitouch_tester.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.multitouch_tester.activities.MultiTouchActivity;
import com.android.multitouch_tester.async_tasks.AddEntryInDbAsyncTask;
import com.android.multitouch_tester.async_tasks.ChangeMaximumTouchesCountAsyncTask;
import com.android.multitouch_tester.auxiliary_classes.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class will represent the view, which will be used to test the multitouch of the screen.
 * Only it will be used to determine the maximum number of touches the screen of the device can register simultaneously.
 */
public class MultiTouchTestView extends View {
    /** The colours, which will be used for the circles following the touches. */
    public static final int[] COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN};

    /** A list of all coordinates for the circles to be drawn. */
    private List<Coordinates> coordinates;

    /** The paint for the draws. */
    private Paint paint;

    /** The parent activity of the view. */
    private MultiTouchActivity activity;


    /** The count of pointers in the last onTouchEvent. Used to determine whether a new touch point was registered.
     * If so to add the new touch to the db and update the textView with total touches. */
    private int lastPointersCount = 0;


    /**
     * Constructor for the view.
     * Initialises the fields.
     *
     * @param context The context of the view (its activity).
     * @param attributeSet Needed only by the Android OS.
     */
    public MultiTouchTestView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Get the activity to use it to update tho maximum count.
        this.activity = (MultiTouchActivity) context;
        this.paint = new Paint();

        // Initialise the list of coordinates for the initial drawing of the view.
        this.coordinates = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Call the drawing method (outside of the onDraw method for code reusability).
        drawTouches(canvas, this.coordinates, this.paint, null);
    }

    /**
     * This is a static method drawing all the circles and lines described by a list of coordinates and their colours.
     * @param canvas the canvas
     * @param coordinates the coordinates of the touches
     * @param paint the paint to draw with
     * @param coordinatesColour the colour of the touches
     */
    public static void drawTouches(Canvas canvas, List<Coordinates> coordinates, Paint paint, Map<Coordinates, Short> coordinatesColour) {
        int currentCount = coordinates.size();

        // Make the canvas black.
        canvas.drawColor(Color.BLACK);

        // Draw a circle  and lines for each of the coordinates.
        for (int i = 0; i < currentCount; i++) {
            Coordinates current = coordinates.get(i);

            // Create 2 local variables storing the coordinates to not call the getters every time.
            float x = current.getX(), y = current.getY();

            // Check whether the method is drawing new touches or old ones.
            if (coordinatesColour == null) {
                // Set the color from the array of colors and draw the circle. With lines around it.
                paint.setColor(COLORS[i % 5]);
            } else {
                // Set the colour from the map.
                paint.setColor(COLORS[coordinatesColour.get(current)]);
            }

            // Set the size for the text for coordinates.
            paint.setTextSize(20);

            // Set the width of the circle.
            paint.setStrokeWidth(20);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(x, y, 70, paint);

            // Set the width of the lines.
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.FILL);

            // Draw the lines and the text information (coordinates).
            canvas.drawText("x = " + x, x + 10, 25, paint);
            canvas.drawLine(x, 0, x, canvas.getHeight(), paint);
            canvas.drawText("y = " + y, 5, y + 25, paint);
            canvas.drawLine(0, y, canvas.getWidth(), y, paint);

            // Draw the number of the specific circle next to it in white colour.
            paint.setColor(Color.WHITE);
            // Set the size for the numbers.
            paint.setTextSize(75);
            canvas.drawText(String.valueOf(i + 1), current.getX() + 110, current.getY(), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        // Delete all previous coordinates.
        this.coordinates = new ArrayList<>();

        // Get the count of simultaneous touches.
        int pointerCount = event.getPointerCount();

        // Check whether the current count is greater than the greatest total count until now.
        // If so to change its entry in the database.
        if (pointerCount > this.activity.getCurrentMax()) {
            // Change the maximum count.
            this.activity.changeMax(pointerCount);
            // Change it in the db.
            new ChangeMaximumTouchesCountAsyncTask(getContext(), pointerCount).execute();
        }

        // For each of the pointers of touches -> Put their coordinates to draw them in onDraw().
        for (int i = 0; i < pointerCount; i++) {
            float x = event.getX(i), y = event.getY(i);

            // Remove points if they are outside of the current view.
            if (x < 0 || y < 0) {
                continue;
            }

            this.coordinates.add(new Coordinates(event.getX(i), event.getY(i)));
        }

        // If the count of pointers has become greater than last time.
        // Then another pointer is started, which should be recorded in the db.
        if (this.lastPointersCount < event.getPointerCount()) {
            new AddEntryInDbAsyncTask(getContext(), event.getX(pointerCount - 1),
                    event.getY(pointerCount - 1), (short) ((pointerCount - 1) % 5), this.activity.getTotalCount()).execute();
        }

        // Save the new pointers count.
        this.lastPointersCount = event.getPointerCount();

        // Draw all the circles, lines and other information on the canvas, calling the onDraw method.
        this.invalidate();

        // To be returned. Whether we need to know about another subsequent events
        boolean toReturn = true;

        // If the last touch pointer is removed -> remove its circle.
        if (event.getAction() == MotionEvent.ACTION_UP) {
            try {
                this.coordinates.remove(0);
            } catch (Exception e){
                // It may have already been removed.
            }

            // Since it was the last removal.
            toReturn = false;
            // Decrement the pointers count to 0 if the last pointer is up.
            this.lastPointersCount--;
        }

        // Returning whether we want to know the movements and the next events.
        return toReturn;
    }
}