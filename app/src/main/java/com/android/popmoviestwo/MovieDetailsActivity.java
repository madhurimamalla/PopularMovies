package com.android.popmoviestwo;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;

import com.android.popmoviestwo.data.MovieContract;
import com.android.popmoviestwo.utils.MovieDetailsJsonUtils;
import com.android.popmoviestwo.utils.MovieReviewsJsonUtils;
import com.android.popmoviestwo.utils.MovieTrailersJsonUtils;
import com.android.popmoviestwo.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailerAdapterOnClickListener {

    private Movie movie;
    private TextView movieTitle;
    private ImageView moviePoster;
    private TextView movieReleaseDate;
    private TextView movieOverview;
    private TextView movieUserRating;
    private TextView mFavButton;

    private RecyclerView recyclerViewReviews;
    private RecyclerView recyclerViewTrailers;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;

    private String mTrailerSummary;
    private String YOUTUBE_CONSTRUCT = "vnd.youtube:";
    private String BROWSER_CONSTRUCT = "http://www.youtube.com/watch?v=";
    private String RATING_OUT_OF_TEN = "/10";
    private final String POPMOVIES_SHARE_HASHTAG = "#PopMovies";
    private final String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/w500//";
    private String SAVE_INSTANCE_KEY = "movie_detail";

    private String TAG = MovieDetailsActivity.class.getSimpleName();

    private void showReviews() {
        recyclerViewReviews.setVisibility(View.VISIBLE);
    }

    private void showTrailers() {
        recyclerViewTrailers.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.movie_details_activity);

        movieTitle = (TextView) findViewById(R.id.movie_orig_title);
        movieOverview = (TextView) findViewById(R.id.movie_overview);
        moviePoster = (ImageView) findViewById(R.id.movie_image);
        movieReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieUserRating = (TextView) findViewById(R.id.movie_user_rating);
        mFavButton = (TextView) findViewById(R.id.mark_as_favorite);
        recyclerViewReviews = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        recyclerViewTrailers = (RecyclerView) findViewById(R.id.recyclerview_trailers);

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
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IMAGE_PATH, movie.getMovieImgPath());
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = createShareTrailerIntent();
                startActivity(shareIntent);
            }
        });
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mTrailerSummary + " " + POPMOVIES_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public void onClick(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_CONSTRUCT + id));
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(BROWSER_CONSTRUCT + id));
        webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (appIntent.resolveActivity(getPackageManager()) != null) {
            // Open Youtube client
            startActivity(appIntent);
        } else {
            // Default to Web browser
            startActivity(webIntent);
        }
    }

    private class FetchMovieDetails extends AsyncTask<String, Void, Movie> {

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

    private void displayMovieDetails(Movie movie) {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewReviews.setLayoutManager(mLayoutManager);
        recyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
        reviewsAdapter = new ReviewsAdapter(this, movie.getMoviereviewsList());
        recyclerViewReviews.setAdapter(reviewsAdapter);
        /**
         * Takes care of showing the reviews
         */
        showReviews();

        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTrailers.setLayoutManager(horizontalLayoutManagaer);
        recyclerViewTrailers.setItemAnimator(new DefaultItemAnimator());
        trailersAdapter = new TrailersAdapter(this, movie.getMovieTrailerList(), this);
        recyclerViewTrailers.setAdapter(trailersAdapter);
        /**
         * Shows the trailers
         */
        showTrailers();

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
        movieUserRating.setText(String.format("%s%s", movie.getUserRating(), RATING_OUT_OF_TEN));
        /**
         * Prepare the mTrailerSummary in case the user wants to share it!
         */
        mTrailerSummary = "Trailer of " + movie.getMovieTitle() + " " + BROWSER_CONSTRUCT + movie.getMovieTrailerList().get(0);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_INSTANCE_KEY, movie);
        super.onSaveInstanceState(outState);
    }
}
