package com.android.popmoviestwo.utils;

/**
 * Created by mmalla on 01/01/18.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: This class is created to assist in parsing and understanding the JSON response given by the 'GET reviews' API of TMDB
 * This class is used to gather the reviews given to a movie
 *
 * TODO Probably need to implement a content provider and then add this data to that content provider instead of writing to the movie object
 */
public class MovieReviewsJsonUtils {

    public static List<String> getMovieReviewsFromJson(String reviewsJsonStr) throws JSONException{

        final String MR_REVIEW_CONTENT = "content";

        final String MR_RESULTS = "results";

        List<String> reviewList = new ArrayList<String>();

        JSONObject moviewReviewsObj = new JSONObject(reviewsJsonStr);

        JSONArray reviewResultsArr = moviewReviewsObj.getJSONArray(MR_RESULTS);

        /**
         * Verifying if the movie doesn't have any reviews, it adds a simple note saying there're no reviews for that movie
         */
        if(reviewResultsArr.length() == 0){
            reviewList.add(0, "No reviews for this movie. Why don't you give one?");
            return reviewList;
        }

        /**
         * Run through the results list and get all the information needed
         */

        for(int i = 0; i < reviewResultsArr.length(); i++){
            JSONObject reviewObj = reviewResultsArr.getJSONObject(i);
            String review_content = reviewObj.getString(MR_REVIEW_CONTENT);
            reviewList.add(i, review_content);
        }
        return reviewList;
    }
}
