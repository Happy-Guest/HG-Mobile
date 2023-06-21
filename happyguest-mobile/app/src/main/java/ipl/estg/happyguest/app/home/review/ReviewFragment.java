package ipl.estg.happyguest.app.home.review;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentReviewBinding;


public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);

        //code here
        binding.btnRegisterReview.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_register_review);
            }
        });

        return binding.getRoot();
    }
}