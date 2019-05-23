package com.android.multitouch_tester.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.multitouch_tester.database.LastTouchesDbContract.LastTouchesEntry;

import java.util.concurrent.Semaphore;

/**
 * A helper class needed for initialising the database and properly using it.
 */
public class LastTouchesDbHelper extends SQLiteOpenHelper {
    /** Create last touches table query */
    private static final String SQL_CREATE_COORDINATES =
            "CREATE TABLE " + LastTouchesEntry.COORDINATES_TABLE_NAME + " (" +
                    LastTouchesEntry._ID + " INTEGER PRIMARY KEY," +
                    LastTouchesEntry.COLUMN_NAME_X_COORDINATE + " REAL," +
                    LastTouchesEntry.COLUMN_NAME_Y_COORDINATE + " REAL," +
                    LastTouchesEntry.COLUMN_NAME_COLOUR + " INTEGER)";

    /** Create maximum touches count table query */
    private static final String SQL_CREATE_MAX_TOUCHES_COUNT =
            "CREATE TABLE " + LastTouchesEntry.MAX_TOUCHES_COUNT_TABLE_NAME + " (" +
                    LastTouchesEntry._ID + " INTEGER PRIMARY KEY," +
                    LastTouchesEntry.COLUMN_NAME_MAX_TOUCHES + " INTEGER);";

    /** Delete last touches table query */
    private static final String SQL_DELETE_COORDINATES =
            "DROP TABLE IF EXISTS " + LastTouchesEntry.COORDINATES_TABLE_NAME;

    /** Delete maximum touches count table query */
    private static final String SQL_DELETE_MAX_TOUCHES_COUNT =
            "DROP TABLE IF EXISTS " + LastTouchesEntry.MAX_TOUCHES_COUNT_TABLE_NAME;

    /** Database version */
    public static final int DATABASE_VERSION = 1;

    /** Database name */
    public static final String DATABASE_NAME = "Touches.db";

    /** Semaphore for making the db thread-safe. */
    private static final Semaphore SEMAPHORE = new Semaphore(1);


    /**
     * Parametrised constructor for the class.
     * @param context the context for teh db
     */
    public LastTouchesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create the tables in the db
     * @param db database
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COORDINATES);
        db.execSQL(SQL_CREATE_MAX_TOUCHES_COUNT);
    }

    /**
     * Overridden thread-safe getReadableDatabase method.
     * @return db
     */
    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = null;
        try {
            SEMAPHORE.acquire();
            db = super.getReadableDatabase();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return db;
    }

    /**
     * Overridden thread-safe getWritableDatabase method.
     * @return db
     */
    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = null;
        try {
            SEMAPHORE.acquire();
            db = super.getWritableDatabase();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return db;
    }

    /**
     * Overridden thread-safe close method.
     */
    @Override
    public synchronized void close() {
        super.close();
        SEMAPHORE.release();
    }

    /**
     * When upgrading the version of the database - destroy and create again.
     * @param db database
     * @param oldVersion old version
     * @param newVersion new version
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_COORDINATES);
        db.execSQL(SQL_DELETE_MAX_TOUCHES_COUNT);
        onCreate(db);
    }

    /**
     * On downgrading the version of the database - destroy and create again
     * @param db database
     * @param oldVersion old version
     * @param newVersion new version
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
