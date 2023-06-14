package ipl.estg.happyguest.app.home.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.utils.others.Token;
import ipl.estg.happyguest.utils.others.User;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private User user;
    private APIRoutes api;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // User, Token and API
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // See if user has codes
        InsertCode insertCode = new InsertCode();
        hasCodesAttempt();

        // Associate code button
        Button btnAssociate = binding.getRoot().findViewById(R.id.btnAssociate);
        btnAssociate.setOnClickListener(v -> insertCode.insertCode(binding, api, user));

        return binding.getRoot();
    }

    private void hasCodesAttempt() {
        Call<HasCodesResponse> call = api.hasCodes(user.getId());
        call.enqueue(new Callback<HasCodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HasCodesResponse> call, @NonNull Response<HasCodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Hide addCode if user has codes
                    if (response.body().hasCodes()) {
                        binding.codeLayout.setVisibility(View.GONE);
                    } else {
                        binding.codeLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.i("HasCodes Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HasCodesResponse> call, @NonNull Throwable t) {
                Log.i("HasCodes Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}