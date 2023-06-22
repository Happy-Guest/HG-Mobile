package ipl.estg.happyguest.app.home.review;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.utils.models.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final ArrayList<Review> reviewsList;
    private final Context context;
    private String order;

    public ReviewsAdapter(ArrayList<Review> reviewsList, Context context) {
        this.reviewsList = reviewsList;
        this.context = context;
        this.order = "DESC";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get Review
        Review review = reviewsList.get(position);

        // Set Texts
        String title;
        if (order.equals("DESC")) {
            title = context.getString(R.string.review_title) + " " + (position + 1);
        } else {
            title = context.getString(R.string.review_title) + " " + (getItemCount() - position);
        }
        holder.id.setText(title);
        holder.date.setText(review.getCreatedAt());

        // View Button
        holder.reviewOpen.setOnClickListener(view -> {
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.changeFragmentBundle(R.id.nav_review, review.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView id;
        private final TextView date;
        private final Button reviewOpen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.txtIdReview);
            date = itemView.findViewById(R.id.txtDateReview);
            reviewOpen = itemView.findViewById(R.id.btnReviewOpen);
        }
    }
}