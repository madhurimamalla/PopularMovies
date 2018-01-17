package com.android.popmoviestwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mmalla on 17/01/18.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.MyTrailerViewHolder> {

    private Context mContext;
    private List<String> trailersList;
    private final TrailersAdapter.TrailerAdapterOnClickListener mListener;

    public interface TrailerAdapterOnClickListener {
        void onClick(String trailerStr);
    }

    public TrailersAdapter(Context context, List<String> trailerList, TrailerAdapterOnClickListener mListener) {
        mContext = context;
        this.trailersList = trailerList;
        this.mListener = mListener;
    }

    @Override
    public MyTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.trailer_list_item, parent, false);
        return new MyTrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyTrailerViewHolder holder, final int position) {
        TextView trailerTextView = (TextView) holder.trailerTextView.findViewById(R.id.trailer_item);
        trailerTextView.setText("View Trailer" + " " + (holder.getAdapterPosition() + 1));
        trailerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trailerStr = trailersList.get(holder.getAdapterPosition());
                mListener.onClick(trailerStr);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailersList.size();
    }

    public class MyTrailerViewHolder extends RecyclerView.ViewHolder {

        public TextView trailerTextView;

        public MyTrailerViewHolder(View itemView) {
            super(itemView);
            trailerTextView = (TextView) itemView.findViewById(R.id.trailer_item);
        }
    }

}
