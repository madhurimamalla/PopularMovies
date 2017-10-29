package com.android.popmoviestwo.utils;

import android.net.Uri;
import android.util.Log;

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

    private static String TAG = NetworkUtils.class.getSimpleName();

    private static String BASE_URL = "https://api.themoviedb.org/3/movie";

    /**
     * API will return data in this format
     */
    private static final String format = "json";

    final static String QUERY_PARAM = "api_key";

    final static String LANG_PARAM = "language";

    final static String PAGE_PARAM = "page";

   /* *//**
     * TODO Insert your API Key here
     */
    final static String API_Key = "<<Insert API key here>>";

    public static URL buildUrl(String path) {

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(QUERY_PARAM, API_Key)
                .appendQueryParameter(LANG_PARAM, "en_US")
                .appendQueryParameter(PAGE_PARAM, "1")
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
     * Description: https://api.themoviedb.org/3/movie/19404?api_key=9f8cc2e73ac5f78cdc4e2bdc6a856a0c&language=en-US
     * @param id
     * @return
     */
    public static URL buildGetMovieDetailsUrl(String id){

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendQueryParameter(QUERY_PARAM,API_Key)
                .appendQueryParameter(LANG_PARAM,"en_US")
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG,"Built URL " + url);
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
}
