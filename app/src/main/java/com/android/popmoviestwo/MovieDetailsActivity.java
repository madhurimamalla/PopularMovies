package com.android.popmoviestwo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.popmoviestwo.utils.MovieDetailsJsonUtils;
import com.android.popmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieReleaseDate;
    private TextView movieOverview;
    private TextView movieUserRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieTitle = (TextView) findViewById(R.id.movie_orig_title);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        moviePoster = (ImageView) findViewById(R.id.movie_image_large);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieUserRating = (TextView) findViewById(R.id.movie_user_rating);

        Intent previousIntent = getIntent();
        String movieId = previousIntent.getStringExtra(Intent.EXTRA_TEXT);
        new FetchMovieDetails().execute(movieId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //TODO fill the details and display them

    public class FetchMovieDetails extends AsyncTask<String, Void, Movie>{

        final String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/w780//";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String id = params[0];
            URL moviesPopularUrl = NetworkUtils.buildGetMovieDetailsUrl(id);
            try {
                String jsonPopularMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesPopularUrl);
                return MovieDetailsJsonUtils.getMovieInformationFromJson(jsonPopularMoviesResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if(movie!=null){
                movieTitle.setText(movie.getMovieTitle());
                Picasso.with(getApplicationContext()).load(IMAGE_MOVIE_URL + movie.getMovieImgPath()).into(moviePoster);
                movieOverview.setText("Overview: " + movie.getOverview());
                movieUserRating.setText("User Rating: " + movie.getUserRating());
                movieReleaseDate.setText("Release Date: " + movie.getReleaseDate());
            }
        }
    }
}
