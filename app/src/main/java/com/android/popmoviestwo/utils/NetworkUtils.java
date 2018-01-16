package com.android.popmoviestwo.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.popmoviestwo.data.MovieContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mmalla on 23/10/17.
 */

/**
 * This class is used to handle the network connections and get the response
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";

    private final static String QUERY_PARAM = "api_key";

    private final static String LANG_PARAM = "language";

    private final static String PAGE_PARAM = "page";

    private final static String PATH_VIDEOS = "videos";

    private final static String PATH_REVIEWS = "reviews";

    private final static String LANG_VALUE = "en_US";

    private final static String PAGE_VALUE = "1";

   /* */
    /**
     * TODO Remove your API Key here before checking in the code
     */
    final static String API_Key = "<<INSERT API KEY here>>";

    public static URL buildUrl(String path) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(QUERY_PARAM, API_Key)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .appendQueryParameter(PAGE_PARAM, PAGE_VALUE)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);
        return url;
    }

    /**
     * Description: https://api.themoviedb.org/3/movie/19404?api_key=<<api-key>></>&language=en-US
     *
     * @param id
     * @return
     */
    public static URL buildGetMovieDetailsUrl(String id) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendQueryParameter(QUERY_PARAM, API_Key)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);
        return url;
    }

    /**
     * https://api.themoviedb.org/3/movie/19404/videos?api_key=<<api-key>></>&language=en-US
     *
     * @param id
     * @return
     */
    public static URL buildGetVideosUrl(String id) {

        Uri builtUrl = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(QUERY_PARAM, API_Key)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .build();

        URL url = null;

        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);
        return url;
    }

    /**
     * https://api.themoviedb.org/3/movie/19404/reviews?api_key=<<api-key>></>&language=en-US&page=1
     *
     * @param id
     * @return
     */

    public static URL buildGetMovieReviewUrl(String id) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(QUERY_PARAM, API_Key)
                .appendQueryParameter(LANG_PARAM, LANG_VALUE)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

   synchronized public static void addInformationToContentProvider(final Context context, final String path) throws Exception{

       Thread newThread = new Thread(new Runnable(){
           @Override
           public void run() {
               ContentResolver movieContentResolver = context.getContentResolver();
               try {
                   URL moviesListUrl = NetworkUtils.buildUrl(path);
                   String jsonPopularMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesListUrl);
                   ContentValues[] contentValues = MoviesListJsonUtils.getMovieContentValuesFromJson(jsonPopularMoviesResponse);
                   if (contentValues != null && contentValues.length != 0) {
                       movieContentResolver.delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                       movieContentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                   }
               }catch (Exception e){
                   e.printStackTrace();
               }
           }
       });
       newThread.start();
       //newThread.join();
    }
}
