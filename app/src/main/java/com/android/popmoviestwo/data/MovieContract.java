package com.android.popmoviestwo.data;

/**
 * Created by mmalla on 07/01/18.
 */

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the table columns for the Movie Database.
 */
public class MovieContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */

    public static final String CONTENT_AUTHORITY = "com.android.popmoviestwo";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for PopMovies2.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    /**
     * Inner class that defines the table contents of the MovieTable
     */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_IMAGE_PATH = "image_path";

        public static final String COLUMN_FAVORITE = "favorite";

    }
}
