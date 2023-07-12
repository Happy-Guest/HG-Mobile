package ipl.estg.happyguest.app.home.review;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentReviewBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ReviewResponse;
import ipl.estg.happyguest.utils.models.Review;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class ReviewFragment extends Fragment {

    private Long reviewId;
    private FragmentReviewBinding binding;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);

        // Get the review id
        if (getArguments() != null) {
            Bundle args = getArguments();
            reviewId = args.getLong("id");
        }

        // API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getReviewAttempt();

        // Close Button
        binding.btnClose.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.nav_reviews);
            }
        });

        return binding.getRoot();
    }

    private void getReviewAttempt() {
        Call<ReviewResponse> call = api.getReview(reviewId);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull retrofit2.Response<ReviewResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Review and populate fields
                    Review review = response.body().getReview();
                    String date = getString(R.string.date) + " " + Objects.requireNonNull(review).getCreatedAt();
                    binding.txtDate.setText(date);
                    fillStars(review.getStars());
                    binding.txtComment.setText(review.getComment() != null ? review.getComment() : getString(R.string.no_comment));
                    binding.checkAuthorization.setChecked(review.getAuthorize() == 1);
                    binding.checkShared.setChecked(review.getShared() == 1);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetReview Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetReview Error: ", t.getMessage());
            }
        });
    }

    private void fillStars(int stars) {
        for (int i = 1; i <= 5; i++) {
            ImageButton starImageView = getStarImageView(i);
            if (i <= stars) {
                starImageView.setImageResource(R.drawable.star_full_icon);
            } else {
                starImageView.setImageResource(R.drawable.star_icon);
            }
        }
    }

    private ImageButton getStarImageView(int star) {
        ImageButton starImageView = null;
        switch (star) {
            case 1:
                starImageView = binding.imageStar1;
                break;
            case 2:
                starImageView = binding.imageStar2;
                break;
            case 3:
                starImageView = binding.imageStar3;
                break;
            case 4:
                starImageView = binding.imageStar4;
                break;
            case 5:
                starImageView = binding.imageStar5;
                break;
        }
        return starImageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}