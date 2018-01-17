package com.android.popmoviestwo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.popmoviestwo.FavMoviesAdapter.FavMoviesAdapterOnClickListener;
import com.android.popmoviestwo.MoviesAdapter.MoviesAdapterOnClickListener;
import com.android.popmoviestwo.data.MovieContract;
import com.android.popmoviestwo.utils.MoviesListJsonUtils;
import com.android.popmoviestwo.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapterOnClickListener, FavMoviesAdapterOnClickListener {

    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private FavMoviesAdapter favMoviesAdapter;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIcon;
    private boolean fav_flag = false;
    private final static String FAV_MOVIES_FLAG = "fav_flag";
    private final static String MOVIE_LIST_SAVE_INSTANCE = "movie_list";
    private final static String PATH_POPULAR_PARAM = "popular";
    private final static String PATH_TOP_RATED_PARAM = "top_rated";

    private ArrayList<Movie> moviesList = new ArrayList<>();

    /*
    * The columns of data that we are interested in displaying within our MainActivity's list of
    * movie data.
    */
    private static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_IMG = 2;

    private static final int ID_MOVIE_LOADER = 77;


    private void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        mLoadingIcon.setVisibility(View.VISIBLE);
    }

    private void showMovieThumbnails() {
        mLoadingIcon.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mErrorMessage = (TextView) findViewById(R.id.error_message);
        mLoadingIcon = (ProgressBar) findViewById(R.id.loading_icon);

        if (savedInstanceState != null) {
            this.fav_flag = savedInstanceState.getBoolean(FAV_MOVIES_FLAG);
        }
        if (fav_flag) {
            getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
        } else {
            if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_LIST_SAVE_INSTANCE)) {
                new FetchMoviesList().execute(PATH_POPULAR_PARAM);
                showGeneralMovieLists();
            } else {
                moviesList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_SAVE_INSTANCE);
                showGeneralMovieLists();
            }
        }
    }

    private void showGeneralMovieLists() {
        /**
         * This is to automatically decide based on the width of the device how many noOfColumns are
         * possible in one row for the display of movie thumbnails
         */
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), noOfColumns);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        favMoviesAdapter = new FavMoviesAdapter(this, this);
        moviesAdapter = new MoviesAdapter(this, moviesList, this);
        recyclerView.setAdapter(moviesAdapter);
        showMovieThumbnails();
    }

    @Override
    public void onClick(Movie movie) {
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(Intent.EXTRA_TEXT, movie.getMovieId());
        startActivity(movieDetailsIntent);
    }

    @Override
    public void onClick(Cursor cursor, int position) {
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        cursor.moveToPosition(position);
        movieDetailsIntent.putExtra(Intent.EXTRA_TEXT, cursor.getString(INDEX_MOVIE_ID));
        startActivity(movieDetailsIntent);
    }

    private class FetchMoviesList extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            recyclerView.setVisibility(View.INVISIBLE);
            mLoadingIcon.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String path = params[0];

            /**
             * Get the URL to fetch the Popular movies
             */
            URL moviesListUrl = NetworkUtils.buildUrl(path);

            try {
                String jsonPopularMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesListUrl);
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
                mErrorMessage.setVisibility(View.INVISIBLE);
                showMovieThumbnails();
                moviesList.clear();
                moviesList.addAll(mList);
                moviesAdapter.notifyDataSetChanged();
            } else {
                showLoading();
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
            fav_flag = false;
            getSupportLoaderManager().destroyLoader(ID_MOVIE_LOADER);
            new FetchMoviesList().execute(PATH_TOP_RATED_PARAM);
            showGeneralMovieLists();
            return true;
        }
        if (id == R.id.action_sort_by_popular) {
            fav_flag = false;
            getSupportLoaderManager().destroyLoader(ID_MOVIE_LOADER);
            new FetchMoviesList().execute(PATH_POPULAR_PARAM);
            showGeneralMovieLists();
            return true;
        }
        if (id == R.id.favorites_list) {
            fav_flag = true;
            displayFavoriteThumbnails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIE_LOADER:
                Uri favMoviesList = MovieContract.MovieEntry.CONTENT_URI;

                String selectionArgs = MovieContract.MovieEntry.COLUMN_FAVORITE + " = 1";

                return new CursorLoader(this,
                        favMoviesList,
                        MAIN_MOVIE_PROJECTION,
                        selectionArgs,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        moviesList.clear();
        int position = 0;
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), noOfColumns);
        recyclerView.setLayoutManager(mLayoutManager);
        favMoviesAdapter = new FavMoviesAdapter(this, this);
        recyclerView.setAdapter(favMoviesAdapter);
        favMoviesAdapter.swapCursor(data);
        if (data.getPosition() == RecyclerView.NO_POSITION) position = 0;
        recyclerView.smoothScrollToPosition(position);
        if (data.getCount() != 0) showMovieThumbnails();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        favMoviesAdapter.swapCursor(null);
    }

    private void displayFavoriteThumbnails() {
        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_SAVE_INSTANCE, moviesList);
        outState.putBoolean(FAV_MOVIES_FLAG, fav_flag);
        super.onSaveInstanceState(outState);
    }
}
