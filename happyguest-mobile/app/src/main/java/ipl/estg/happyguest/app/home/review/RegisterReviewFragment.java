package ipl.estg.happyguest.app.home.review;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentRegisterReviewBinding;


public class RegisterReviewFragment extends Fragment {

    public FragmentRegisterReviewBinding binding;
    private int currentStar = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterReviewBinding.inflate(inflater, container, false);

        // Set click listeners for all stars
        binding.imageStar1.setOnClickListener(v -> updateStars(1));
        binding.imageStar2.setOnClickListener(v -> updateStars(2));
        binding.imageStar3.setOnClickListener(v -> updateStars(3));
        binding.imageStar4.setOnClickListener(v -> updateStars(4));
        binding.imageStar5.setOnClickListener(v -> updateStars(5));
        
        return binding.getRoot();
    }

    private void updateStars(int selectedStar) {
        currentStar = selectedStar;
        for (int i = 1; i <= 5; i++) {
            ImageButton starImageView = getStarImageView(i);
            if (i <= selectedStar) {
                starImageView.setVisibility(View.INVISIBLE);
                starImageView.setImageResource(R.drawable.star_full_icon);
                starImageView.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                starImageView.setVisibility(View.VISIBLE);
            } else {
                starImageView.setImageResource(R.drawable.star_icon);
            }
        }
    }

    private ImageButton getStarImageView(int star) {
        String starImageId = "imageStar" + star;
        int starImageViewId = getResources().getIdentifier(starImageId, "id", requireActivity().getPackageName());
        return requireView().findViewById(starImageViewId);
    }
}