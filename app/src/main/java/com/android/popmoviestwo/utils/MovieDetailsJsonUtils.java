package com.android.popmoviestwo.utils;

/**
 * Created by mmalla on 27/10/17.
 */

import com.android.popmoviestwo.Movie;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: This class is created to assist in parsing and understanding the JSON response given by the GET movie details of TMDB
 * This class is used to parse through the API which gives the movie details
 */
public class MovieDetailsJsonUtils {

    public static Movie getMovieInformationFromJson(String moviesJsonStr) throws JSONException {

        /**
         * Movie release date
         */
        final String PM_MOVIE_RELEASE_DATE = "release_date";

        /**
         * Movie id
         */
        final String PM_MOVIE_ID = "id";

        /**
         * Movie title
         */
        final String PM_MOVIE_TITLE = "original_title";

        /**
         * Image path
         */
        final String PM_IMG_PATH = "poster_path";

        /**
         * Overview of the movie
         */
        final String PM_OVERVIEW = "overview";

        /**
         * user rating for the movie
         */
        final String PM_VOTE_AVG = "vote_average";

        Movie parsedMovieDetails = new Movie("", "", "");

        JSONObject movieJson = new JSONObject(moviesJsonStr);

        String releaseDate = movieJson.getString(PM_MOVIE_RELEASE_DATE);
        String overview = movieJson.getString(PM_OVERVIEW);
        String movieTitle = movieJson.getString(PM_MOVIE_TITLE);
        String movieImgPath = movieJson.getString(PM_IMG_PATH);
        String userRating = movieJson.getString(PM_VOTE_AVG);
        String movieId = movieJson.getString(PM_MOVIE_ID);

        parsedMovieDetails.setMovieTitle(movieTitle);
        parsedMovieDetails.setMovieId(movieId);
        parsedMovieDetails.setReleaseDate(releaseDate);
        parsedMovieDetails.setMovieImgPath(movieImgPath);
        parsedMovieDetails.setOverview(overview);
        parsedMovieDetails.setUserRating(userRating);

        return parsedMovieDetails;
    }
}
