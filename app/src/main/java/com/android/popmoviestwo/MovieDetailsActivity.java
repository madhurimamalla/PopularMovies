package com.android.popmoviestwo;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popmoviestwo.data.MovieContract;
import com.android.popmoviestwo.utils.MovieDetailsJsonUtils;
import com.android.popmoviestwo.utils.MovieReviewsJsonUtils;
import com.android.popmoviestwo.utils.MovieTrailersJsonUtils;
import com.android.popmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity {

    private Movie movie;
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieReleaseDate;
    private TextView movieOverview;
    private TextView movieUserRating;
    private TextView movieVideoTrailer;
    private TextView mFavButton;

    private RecyclerView recyclerViewReviews;
    private ReviewsAdapter reviewsAdapter;

    private String YOUTUBE_CONSTRUCT = "vnd.youtube:";
    private String BROWSER_CONSTRUCT = "http://www.youtube.com/watch?v=";
    private String RATING_OUT_OF_TEN = "/10";
    private String youtube_id;
    private final String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/w500//";
    private String SAVE_INSTANCE_KEY = "movie_detail";

    private String TAG = MovieDetailsActivity.class.getSimpleName();

    public void showReviews(){
        recyclerViewReviews.setVisibility(View.VISIBLE);
    }

    public void hideReviews(){
        recyclerViewReviews.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity);

        movieTitle = (TextView) findViewById(R.id.movie_orig_title);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        moviePoster = (ImageView) findViewById(R.id.movie_image);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieUserRating = (TextView) findViewById(R.id.movie_user_rating);
        mFavButton = (TextView) findViewById(R.id.mark_as_favorite);
        movieVideoTrailer = (TextView) findViewById(R.id.video_trailer_link);
        recyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerview_reviews);

        /**
         * Creating an OnClickListener so it opens a Youtube app or a browser on clicking a movie Video Trailer TextView element
         */
        movieVideoTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = youtube_id;
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_CONSTRUCT + id));
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(BROWSER_CONSTRUCT + id));
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    getApplicationContext().startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                    getApplicationContext().startActivity(webIntent);
                }
            }
        });

        /**
         * Creating an onClickListener so it adds this particular movie to the user's list of favorite movies
         */
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateFavMoviesList() > 0) {
                    Log.v(TAG, "This movie has been added to your favorites");
                            /*
                            Lets the user know that the movie has been added to his/her favourites.
                             */
                    Toast.makeText(MovieDetailsActivity.this, "This movie has been added to your favorites", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Description: Adds the movie to the list of favorites
             * @return the number of movies added to the favorites
             */
            private int updateFavMoviesList() {
                /**
                 * Creating a single ContentValues object and adding to the list of ContentValues
                 */
                ContentValues[] contentValuesList = new ContentValues[1];
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, true);
                Log.v(TAG, "Movie id: " + movie.getMovieId());
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE_PATH, movie.getMovieImgPath());
                Log.v(TAG, "Movie title: " + movie.getMovieTitle());
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getMovieTitle());
                contentValuesList[0] = contentValues;
                return getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesList);
            }
        });

        Intent previousIntent = getIntent();
        String movieId = previousIntent.getStringExtra(Intent.EXTRA_TEXT);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_INSTANCE_KEY)) {
            movie = savedInstanceState.getParcelable(SAVE_INSTANCE_KEY);
            displayMovieDetails(movie);
        } else {
            new FetchMovieDetails().execute(movieId);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public class FetchMovieDetails extends AsyncTask<String, Void, Movie> {

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

                movie = MovieDetailsJsonUtils.getMovieInformationFromJson(jsonMovieDetailsResponse);

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
        protected void onPostExecute(final Movie movie) {
            if (movie != null) {
                displayMovieDetails(movie);
            }
        }
    }

    public void displayMovieDetails(Movie movie) {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewReviews.setLayoutManager(mLayoutManager);
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        reviewsAdapter = new ReviewsAdapter(this, movie.getMoviereviewsList());
        recyclerViewReviews.setAdapter(reviewsAdapter);
        /**
         * Takes care of showing the reviews
         */
        showReviews();

        movieTitle.setText(movie.getMovieTitle());
        try {
            Picasso.with(getApplicationContext()).load(IMAGE_MOVIE_URL + movie.getMovieImgPath()).error(R.drawable.user_placeholder_error).into(moviePoster);
        } catch (IllegalArgumentException e) {
            moviePoster.setImageResource(R.drawable.user_placeholder_error);
        }

        movieOverview.setText(movie.getOverview());
        /**
         * Display the Release date
         */
        movieReleaseDate.setText(movie.getReleaseDate());
        /**
         * Display the User rating
         */
        movieUserRating.setText(movie.getUserRating() + RATING_OUT_OF_TEN);
        youtube_id = movie.getMovieTrailerList().get(0);
        movieVideoTrailer.setText("View Trailer");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_INSTANCE_KEY, movie);
        super.onSaveInstanceState(outState);
    }
}
