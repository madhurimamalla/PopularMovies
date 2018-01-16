package com.android.popmoviestwo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.popmoviestwo.data.MovieContract.MovieEntry;


/**
 * Created by mmalla on 07/01/18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABASE_VERSION = 7;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MovieEntry.COLUMN_MOVIE_ID + " STRING NULL, "
                + MovieEntry.COLUMN_MOVIE_TITLE + " STRING NULL, "
                + MovieEntry.COLUMN_MOVIE_IMAGE_PATH + " STRING NULL, "
                + MovieEntry.COLUMN_FAVORITE + " BOOLEAN, "
                + "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
