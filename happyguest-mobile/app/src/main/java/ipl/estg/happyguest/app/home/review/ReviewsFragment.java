package ipl.estg.happyguest.app.home.review;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentReviewsBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ReviewsResponse;
import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Review;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsFragment extends Fragment {

    private FragmentReviewsBinding binding;
    private User user;
    private APIRoutes api;
    private String order = "DESC";
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> reviewsList;
    private Meta meta;
    private int screenHeight;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewsBinding.inflate(inflater, container, false);

        // User, API, Token and HasCodes
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);
        HasCodes hasCodes = new HasCodes(binding.getRoot().getContext());

        // Change register button
        binding.btnRegisterReview.setEnabled(hasCodes.getHasCode());

        // Reviews
        RecyclerView reviewsRV = binding.reviewsRV;
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(reviewsList, binding.getRoot().getContext());
        reviewsRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        reviewsRV.setAdapter(reviewsAdapter);

        // Set the minimum height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.3));

        // Register review button
        binding.btnRegisterReview.setOnClickListener(v -> {
            // Verify if the user made a review in the last week
            binding.btnRegisterReview.setEnabled(false);
            if (user.getLastReview() != null) {
                try {
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    Date lastReview = dateFormat.parse(user.getLastReview());
                    if (lastReview != null && System.currentTimeMillis() - lastReview.getTime() < 7 * 24 * 60 * 60 * 1000) {
                        Toast.makeText(getContext(), getString(R.string.review_week), Toast.LENGTH_SHORT).show();
                        binding.btnRegisterReview.setEnabled(true);
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // Change fragment
            if (getActivity() instanceof HomeActivity) {
                binding.btnRegisterReview.setEnabled(true);
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_register_review);
            }
        });

        // Get reviews on scroll
        binding.reviewsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView codesRV, int dx, int dy) {
                super.onScrolled(codesRV, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) codesRV.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == reviewsList.size() - 1) {
                    if (meta != null && meta.getCurrentPage() < meta.getLastPage()) {
                        getReviewsAttempt(meta.getCurrentPage() + 1);
                    }
                }
            }
        });

        // Swipe to refresh reviews
        binding.swipeRefresh.setOnRefreshListener(this::getReviews);

        // Switch order
        binding.switchOrderReviews.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) order = "DESC";
            else order = "ASC";
            reviewsAdapter.setOrder(order);
            if (binding.switchOrderReviews.isEnabled()) {
                getReviews();
            }
        });

        // Get reviews
        binding.switchOrderReviews.setEnabled(false);
        new Handler().postDelayed(() -> getReviewsAttempt(1), 200);

        return binding.getRoot();
    }

    private void getReviews() {
        binding.switchOrderReviews.setEnabled(false);
        int previousItemCount = reviewsList.size();
        reviewsList.clear();
        getReviewsAttempt(1);

        int newItemCount = reviewsList.size();
        if (newItemCount > previousItemCount) {
            reviewsAdapter.notifyItemRangeInserted(previousItemCount, newItemCount - previousItemCount);
        } else if (newItemCount < previousItemCount) {
            reviewsAdapter.notifyItemRangeRemoved(newItemCount, previousItemCount - newItemCount);
        } else {
            reviewsAdapter.notifyItemRangeChanged(0, newItemCount);
        }
        binding.swipeRefresh.setRefreshing(false);
    }

    private void getReviewsAttempt(int page) {
        Call<ReviewsResponse> call = api.getUserReviews(user.getId(), page, order);
        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsResponse> call, @NonNull Response<ReviewsResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                binding.switchOrderReviews.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Save reviews and update the adapter
                    int lastPos = reviewsList.size();
                    ArrayList<Review> reviews = response.body().getData();
                    reviewsList.addAll(reviews);
                    meta = response.body().getMeta();
                    reviewsAdapter.notifyItemRangeInserted(lastPos, reviews.size());
                    if (reviewsList.size() == 0) {
                        binding.txtNoReviews.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.txtNoReviews.setVisibility(View.VISIBLE);
                        binding.swipeRefresh.setMinimumHeight((int) (screenHeight / 1.7));
                    } else {
                        binding.txtNoReviews.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.txtNoReviews.setVisibility(View.GONE);
                        binding.swipeRefresh.setMinimumHeight(screenHeight - 210);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.reviews_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetReviews Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.reviews_error), Toast.LENGTH_SHORT).show();
                Log.i("GetReviews Error: ", t.getMessage());
                binding.switchOrderReviews.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}