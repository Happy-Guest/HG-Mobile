package ipl.estg.happyguest.app.home.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.databinding.InsertCodeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        InsertCodeBinding insertCodeBinding = InsertCodeBinding.inflate(inflater, container, false);

        // Se o user não tiver nenhum código associado, mostra o botão para associar um código
        InsertCode insertCode = new InsertCode();

        insertCodeBinding.btnAssociate.setOnClickListener(v -> insertCode.insertCode(insertCodeBinding));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}