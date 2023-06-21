package ipl.estg.happyguest.app.home.complaint;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentRegisterComplaintBinding;

public class RegisterComplaintFragment extends Fragment {

    private FragmentRegisterComplaintBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterComplaintBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}