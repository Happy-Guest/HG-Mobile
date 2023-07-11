package ipl.estg.happyguest.app.home.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.databinding.FragmentObjectsBinding;

public class ObjectsFragment extends Fragment {

    private FragmentObjectsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentObjectsBinding.inflate(inflater, container, false);

        // Code here

        return binding.getRoot();
    }
}