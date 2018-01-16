package com.android.popmoviestwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mmalla on 16/01/18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyReviewViewHolder> {

    private Context mContext;
    private List<String> reviewsList;

    public ReviewsAdapter(Context context, List<String> listOfReviews) {
        this.reviewsList = listOfReviews;
        mContext = context;
    }

    public class MyReviewViewHolder extends RecyclerView.ViewHolder{

        public TextView reviewText;

        public MyReviewViewHolder(View itemView) {
            super(itemView);
            reviewText = (TextView) itemView.findViewById(R.id.review_item);
        }
    }

    @Override
    public MyReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.review_list_item, parent, false);
        return new MyReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyReviewViewHolder holder, int position) {
        String reviewStr = reviewsList.get(position);
        TextView reviewTextView = (TextView) holder.reviewText.findViewById(R.id.review_item);
        reviewTextView.setText("Review " + (position+1) + "\n" + reviewStr);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }
}
