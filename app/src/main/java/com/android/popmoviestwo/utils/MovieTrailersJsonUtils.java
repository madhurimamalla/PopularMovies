package com.android.popmoviestwo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmalla on 01/01/18.
 * Description: This class is created to assist in parsing and understanding the JSON response given by the 'GET videos' API of TMDB
 * We retrieve a list of key strings of both Trailers/Featurettes listed
 */

public class MovieTrailersJsonUtils {

    public static List<String> getMovieTrailersListFromJson(String trailersJsonResponseStr) throws JSONException{

        final String TL_RESULTS = "results";

        final String TL_TYPE = "type";

        final String TL_KEY = "key";

        final String TRAILER_STRING = "Trailer";

        final String FEATURETTE_STRING = "Featurette";

        List<String> movieTrailersList = new ArrayList<String>();

        JSONObject movieTrailersJSONObj = new JSONObject(trailersJsonResponseStr);

        JSONArray movieTrailersArr = movieTrailersJSONObj.getJSONArray(TL_RESULTS);

        /**
         * If there are no results in the response, attach a note and return the list
         */
        if(movieTrailersArr.length() == 0){
            movieTrailersList.add(0, "There are no trailers attached to the movie!");
            return movieTrailersList;
        }

        /**
         * Use the int j to keep a count of the list of trailer keys
         */
        int j = 0;
        /**
         * Use i to parse through all the results of the JSON
         */
        for(int i = 0; i < movieTrailersArr.length(); i++){
            JSONObject tentativeTrailerObj = movieTrailersArr.getJSONObject(i);
            String key = tentativeTrailerObj.getString(TL_KEY).trim().toString();
            String videoType = tentativeTrailerObj.getString(TL_TYPE).trim().toString();

            // TODO Remove Featurette string if it's not needed for the app
            if(videoType.equals(TRAILER_STRING) || videoType.equals(FEATURETTE_STRING)){
                movieTrailersList.add(j, key);
                j++;
            }
        }

        if(j == 0){
            movieTrailersList.add(0, "There are no trailers attached to the movie!");
            return movieTrailersList;
        }

        return movieTrailersList;
    }
}
