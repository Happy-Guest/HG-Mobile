package ipl.estg.happyguest.app.home.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentReviewBinding;
import ipl.estg.happyguest.utils.storage.User;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);

        User user = new User(binding.getRoot().getContext());

        binding.btnRegisterReview.setOnClickListener(v -> {
            // TODO: Verificar se pode fazer a review
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_register_review);
            }
        });

        return binding.getRoot();
    }
}