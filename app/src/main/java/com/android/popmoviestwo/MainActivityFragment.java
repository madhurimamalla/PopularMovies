package com.android.popmoviestwo;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.popmoviestwo.utils.NetworkUtils;
import com.android.popmoviestwo.utils.MoviesListJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmalla on 26/10/17.
 */

public class MainActivityFragment extends Fragment{

    private MoviesAdapter movieAdapter;
    final static String PATH_POPULAR_PARAM = "popular";
    final static String PATH_TOP_RATED_PARAM = "top_rated";
    public List<Movie> moviesList = new ArrayList<>();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //new FetchMoviesList().execute(PATH_POPULAR_PARAM);
        new FetchMoviesList().execute(PATH_TOP_RATED_PARAM);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        movieAdapter = new MoviesAdapter(getActivity(), moviesList);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(movieAdapter);
        return rootView;
    }


    public class FetchMoviesList extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String path = params[0];

            URL moviesPopularUrl = NetworkUtils.buildUrl(path);

            try {
                String jsonPopularMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesPopularUrl);
                Log.v("PopularMovies: ", jsonPopularMoviesResponse);

                List<Movie> simpleJsonMoviesData = MoviesListJsonUtils.getSimpleMoviesInformationFromJson(getContext(), jsonPopularMoviesResponse);
                return simpleJsonMoviesData;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(List<Movie> mList) {
            if (mList != null) {
                moviesList.addAll(mList);
                movieAdapter.notifyDataSetChanged();
            } else {
                //showErrorMessage();
            }
        }
    }
}
