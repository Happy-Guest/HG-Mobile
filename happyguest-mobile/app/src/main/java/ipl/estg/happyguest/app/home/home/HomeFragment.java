package ipl.estg.happyguest.app.home.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.User;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private User user;
    private APIRoutes api;
    private Token token;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // User, Token and API
        user = new User(binding.getRoot().getContext());
        token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // TODO: Se o user não tiver nenhum código associado, mostra o botão para associar um código
        InsertCode insertCode = new InsertCode();

        // Associate code button
        Button btnAssociate = binding.getRoot().findViewById(R.id.btnAssociate);
        btnAssociate.setOnClickListener(v -> insertCode.insertCode(binding, api, user));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}