package ipl.estg.happyguest.app.home.code;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.databinding.FragmentCodeBinding;

public class CodeFragment extends Fragment {

    private FragmentCodeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCodeBinding.inflate(inflater, container, false);

        // Code here

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}