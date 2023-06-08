package ipl.estg.happyguest.app.home.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.databinding.InsertCodeBinding;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        InsertCodeBinding insertCodeBinding = InsertCodeBinding.inflate(inflater, container, false);

        // Se o user não tiver nenhum código associado, mostra o botão para associar um código
        InsertCode insertCode = new InsertCode();

        Button btnAssociate = binding.getRoot().findViewById(R.id.btnAssociate);
        btnAssociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCode.insertCode(binding);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}