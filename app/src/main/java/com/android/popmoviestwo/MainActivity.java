package com.android.popmoviestwo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.android.popmoviestwo.MoviesAdapter.MoviesAdapterOnClickListener;
import com.android.popmoviestwo.utils.MoviesListJsonUtils;
import com.android.popmoviestwo.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickListener{

    // TODO Try to add some loading icon and Error messages at the end the project
    private MoviesAdapter movieAdapter;
    private GridView gridView;
    private final static String PATH_POPULAR_PARAM = "popular";
    private final static String PATH_TOP_RATED_PARAM = "top_rated";
    private List<Movie> moviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new FetchMoviesList().execute(PATH_POPULAR_PARAM);
        gridView = (GridView) findViewById(R.id.movies_grid);
        movieAdapter = new MoviesAdapter(this, moviesList, this);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(movieAdapter);
    }

    @Override
    public void onClick(Movie movie) {
        Intent movieDetailsIntent = new Intent(this,MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(Intent.EXTRA_TEXT,movie.getMovieId());
        startActivity(movieDetailsIntent);
    }

    private class FetchMoviesList extends AsyncTask<String, Void, List<Movie>> {

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
                return MoviesListJsonUtils.getSimpleMoviesInformationFromJson(jsonPopularMoviesResponse);
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
                moviesList.clear();
                moviesList.addAll(mList);
                movieAdapter.notifyDataSetChanged();
            } else {
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_top_rated) {
            new FetchMoviesList().execute(PATH_TOP_RATED_PARAM);
            return true;
        }
        if (id == R.id.action_sort_by_popular) {
            new FetchMoviesList().execute(PATH_POPULAR_PARAM);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
