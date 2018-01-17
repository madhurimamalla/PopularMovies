package com.android.popmoviestwo;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mmalla on 05/01/18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private Context mContext;

    private final MoviesAdapter.MoviesAdapterOnClickListener mListener;

    private List<Movie> moviesList;

    public interface MoviesAdapterOnClickListener{
        void onClick(Movie movie);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView movie_thumbnail;

        public MyViewHolder(View view) {
            super(view);
            movie_thumbnail = (ImageView) view.findViewById(R.id.movie_image);
        }
    }

    public MoviesAdapter(Context context, List<Movie> moviesList, MoviesAdapter.MoviesAdapterOnClickListener listener) {
        this.moviesList = moviesList;
        mContext = context;
        mListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Movie movie = moviesList.get(holder.getAdapterPosition());

        ImageView movie_thumbnail = (ImageView) holder.movie_thumbnail.findViewById(R.id.movie_image);

        movie_thumbnail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Movie movie = moviesList.get(position);
                mListener.onClick(movie);
            }
        });

        try{
            String IMAGE_MOVIE_URL = "http://image.tmdb.org/t/p/w185//";
            Picasso.with(mContext).load(IMAGE_MOVIE_URL + movie.getMovieImgPath()).error(R.drawable.user_placeholder_error).into(movie_thumbnail);
        } catch(IllegalArgumentException e){
            movie_thumbnail.setImageResource(R.drawable.user_placeholder_error);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
