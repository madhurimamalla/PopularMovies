package com.android.popmoviestwo;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by mmalla on 14/01/18.
 */

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavMoviewsViewHolder> {

    private final Context mContext;

    private final String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/w185//";

    private Cursor mCursor;

    public FavMoviesAdapter(@NonNull Context mContext) {
        this.mContext = mContext;

    }

    @Override
    public FavMoviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId;
        layoutId = R.layout.movie_list_row;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new FavMoviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavMoviewsViewHolder viewHolder, int position) {

        mCursor.moveToPosition(position);
        String img = mCursor.getString(MainActivity.INDEX_MOVIE_IMG);

        try {
            Picasso.with(mContext).load(IMAGE_MOVIE_URL + img).error(R.drawable.user_placeholder_error).into(viewHolder.movieThumbnail);
        } catch (IllegalArgumentException e) {
            viewHolder.movieThumbnail.setImageResource(R.drawable.user_placeholder_error);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class FavMoviewsViewHolder extends RecyclerView.ViewHolder {

        final ImageView movieThumbnail;

        public FavMoviewsViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.movie_image);
        }
    }
}
