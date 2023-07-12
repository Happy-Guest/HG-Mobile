package ipl.estg.happyguest.app.home.reserve;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentReservesBinding;


public class ReservesFragment extends Fragment {

    private FragmentReservesBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReservesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}