package com.android.popmoviestwo.utils;

/**
 * Created by mmalla on 23/10/17.
 */

import com.android.popmoviestwo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: This class is created to assist in parsing and understanding the JSON response given by APIs from TMDB
 * This parses data from the The Movie DataBase APIs explained in:  https://developers.themoviedb.org/3/movies/get-top-rated-movies &
 * https://developers.themoviedb.org/3/movies/get-popular-movies having by paths /movie/popular & /movie/top_rated.
 */
public class MoviesListJsonUtils {

    public static List<Movie> getSimpleMoviesInformationFromJson(String moviesJsonStr) throws JSONException {

        final String PM_RESULTS = "results";

        final String PM_MOVIE_ID = "id";

        final String PM_MOVIE_TITLE = "title";

        final String PM_IMG_PATH = "poster_path";

        List<Movie> parsedMovieResults = null;

        JSONObject movieJson = new JSONObject(moviesJsonStr);

        JSONArray movielist = movieJson.getJSONArray(PM_RESULTS);

        parsedMovieResults = new ArrayList<>();

        /**
         * Run through the results list and get all the information needed
         */
        for (int i = 0; i < movielist.length(); i++) {
            JSONObject movieData = movielist.getJSONObject(i);

            Movie movie_object = new Movie(movieData.getString(PM_MOVIE_TITLE), movieData.getString(PM_IMG_PATH), movieData.getString(PM_MOVIE_ID));
            parsedMovieResults.add(movie_object);
        }
        return parsedMovieResults;
    }
}
