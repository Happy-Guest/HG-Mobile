package ipl.estg.happyguest.app.home.review;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.MainActivity;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentReviewBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ReviewResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import ipl.estg.happyguest.utils.models.Review;
import ipl.estg.happyguest.utils.models.UserCode;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private APIRoutes api;
    Long reviewId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            Bundle args = getArguments();
            reviewId = args.getLong("id");
        }

        // API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        populateReview();

        return binding.getRoot();
    }

    private void populateReview() {
        Call<ReviewResponse> call = api.getReview(reviewId);
        call.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull retrofit2.Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Review review = new Review(response.body().getId(), response.body().getStars(), response.body().getComment(), response.body().getAutorize(), response.body().getShared(), response.body().getCreatedAt());

                    Toast.makeText(binding.getRoot().getContext(), review.getStars(), Toast.LENGTH_SHORT).show();
                    /*
                    fillStars(response.body().getStars());
                    String date = getString(R.string.date) + ": " +response.body().getCreatedAt();
                    binding.txtDate.setText(date);
                    binding.txtComment.setText(response.body().getComment() != null ? response.body().getComment() : getString(R.string.no_comment));
                    */
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.restore_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetReview Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
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