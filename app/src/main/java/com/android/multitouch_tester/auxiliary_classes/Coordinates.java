package com.android.multitouch_tester.auxiliary_classes;

/**
 * This classed is used in the MultiTouchTestView to store
 * the coordinates of the registered touches.
 */
public class Coordinates {
    /** The x coordinate. */
    private float x;
    /** The y coordinate. */
    private float y;

    /**
     * Parametrised constructor for the class.
     * Initialises the fields.
     * @param x coordinate
     * @param y coordinate
     */
    public Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for the x coordinate.
     * @return x coordinate.
     */
    public float getX() {
        return this.x;
    }

    /**
     * Getter for the y coordinate.
     * @return y coordinate.
     */
    public float getY() {
        return this.y;
    }
}
