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
import com.android.popmoviestwo.utils.MovieReviewsJsonUtils;
import com.android.popmoviestwo.utils.MovieTrailersJsonUtils;
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
    private TextView movieReview;
    private TextView movieVideoTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_3);

        movieTitle = (TextView) findViewById(R.id.movie_orig_title);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        moviePoster = (ImageView) findViewById(R.id.movie_image_large);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieUserRating = (TextView) findViewById(R.id.movie_user_rating);
        movieReview = (TextView) findViewById(R.id.movie_review);
        movieVideoTrailer = (TextView) findViewById(R.id.trailer_video);

        // TODO Add the intent to open the link in Youtube or a chosen browser
        movieVideoTrailer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v.findViewById(R.id.trailer_video);
                String key = textView.getText().toString();
            }
        });

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



    public class FetchMovieDetails extends AsyncTask<String, Void, Movie> {

        final String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/w500//";

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

            /**
             * Get the URL to fetch a movie's details via it's ID
             */
            URL movieDetailUrl = NetworkUtils.buildGetMovieDetailsUrl(id);
            /**
             * Get the URL to fetch the reviews of a movie via ID
             */
            URL getReviewsUrl = NetworkUtils.buildGetMovieReviewUrl(id);
            /**
             * Get the URL to fetch the videos attached to a movie via ID
             */
            URL getVideosUrl = NetworkUtils.buildGetVideosUrl(id);
            try {
                /**
                 * Gather required information you need about one movie
                 */
                String jsonMovieDetailsResponse = NetworkUtils.getResponseFromHttpUrl(movieDetailUrl);
                String jsonMovieReviewsResponse = NetworkUtils.getResponseFromHttpUrl(getReviewsUrl);
                String jsonMovieVideosResponse = NetworkUtils.getResponseFromHttpUrl(getVideosUrl);

                Movie movie = MovieDetailsJsonUtils.getMovieInformationFromJson(jsonMovieDetailsResponse);

                /**
                 * Add the movie reviews to it's respective movie object
                 */
                movie.setMoviereviewsList(MovieReviewsJsonUtils.getMovieReviewsFromJson(jsonMovieReviewsResponse));

                /**
                 * Add the movie trailers to it's respective movie object
                 */
                movie.setMovieTrailerList(MovieTrailersJsonUtils.getMovieTrailersListFromJson(jsonMovieVideosResponse));

                return movie;
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
            if (movie != null) {
                movieTitle.setText(movie.getMovieTitle());
                Picasso.with(getApplicationContext()).load(IMAGE_MOVIE_URL + movie.getMovieImgPath()).into(moviePoster);
                /**
                 * Display the Movie Overview
                 */
                movieOverview.setText(movie.getOverview());
                /**
                 * Display the User rating
                 */
                movieUserRating.setText(movie.getUserRating());
                /**
                 * Display the Release date
                 */
                movieReleaseDate.setText(movie.getReleaseDate());
                /**
                 * Display the movie review
                 */

                // TODO Need to show more than one review in a List/linearList/LinearLayout
                movieReview.setText(movie.getMoviereviewsList().get(0));

                // TODO Need to show more than one video trailer if applicable which on clicking should open the Youtube app
                movieVideoTrailer.setText(movie.getMovieTrailerList().get(0));
            }
        }
    }
}
