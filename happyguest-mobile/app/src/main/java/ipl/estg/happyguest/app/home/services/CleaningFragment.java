package ipl.estg.happyguest.app.home.services;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentCleaningBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ServiceResponse;
import ipl.estg.happyguest.utils.models.Service;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class CleaningFragment extends Fragment {

    private FragmentCleaningBinding binding;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCleaningBinding.inflate(inflater, container, false);

        // API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        getServiceAttempt();

        return binding.getRoot();
    }

    private void getServiceAttempt() {
        Call<ServiceResponse> call = api.getService(1L);
        call.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServiceResponse> call, @NonNull retrofit2.Response<ServiceResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Service and populate fields
                    Service service = response.body().getService();
                    String schedule = getString(R.string.services_schedule) + " " + Objects.requireNonNull(service).getSchedule();
                    binding.txtServiceSchedule.setText(schedule);
                    if (service.getEmail() != null) {
                        binding.txtServiceEmail.setVisibility(View.VISIBLE);
                        binding.txtEmail.setVisibility(View.VISIBLE);
                        binding.txtServiceEmail.setText(service.getEmail());
                    } else {
                        binding.txtServiceEmail.setVisibility(View.GONE);
                        binding.txtEmail.setVisibility(View.GONE);
                    }
                    if (service.getPhone() != null) {
                        binding.txtServicePhone.setVisibility(View.VISIBLE);
                        binding.txtPhone.setVisibility(View.VISIBLE);
                        binding.txtServicePhone.setText(String.format(Locale.getDefault(), "%d", service.getPhone()));
                    } else {
                        binding.txtServicePhone.setVisibility(View.GONE);
                        binding.txtPhone.setVisibility(View.GONE);
                    }
                    binding.txtDescription.setText(service.getDescription());
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetService Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServiceResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("GetService Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}