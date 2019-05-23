package com.android.multitouch_tester.database;

import android.provider.BaseColumns;

/**
 * Contract for using the LastTouchesDB
 */
public class LastTouchesDbContract {
    /**
     * Private constructor to prevent outside creation.
     */
    private LastTouchesDbContract() {}

    /**
     * Contains all needed strings for the db.
     */
    public static class LastTouchesEntry implements BaseColumns {
        public static final String COORDINATES_TABLE_NAME = "last_touches_coordinates";
        public static final String COLUMN_NAME_COLOUR = "colour";
        public static final String COLUMN_NAME_X_COORDINATE = "x_coordinate";
        public static final String COLUMN_NAME_Y_COORDINATE = "y_coordinate";
        public static final String MAX_TOUCHES_COUNT_TABLE_NAME = "max_touches_count";
        public static final String COLUMN_NAME_MAX_TOUCHES = "count";
    }
}
