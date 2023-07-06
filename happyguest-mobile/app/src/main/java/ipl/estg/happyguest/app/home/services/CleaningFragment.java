package ipl.estg.happyguest.app.home.services;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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

    private String formatSchedule(String schedule) {
        // String is 9-14-15-21 and we want 9h-14h and 15h-21h
        String[] scheduleArray = schedule.split("-");
        StringBuilder scheduleString = new StringBuilder();
        for (int i = 0; i < scheduleArray.length; i++) {
            if (i % 2 == 0) {
                scheduleString.append(scheduleArray[i]).append("h-");
            } else {
                scheduleString.append(scheduleArray[i]).append("h");
                if (i != scheduleArray.length - 1) {
                    scheduleString.append(" e ");
                }
            }
        }
        if (scheduleString.toString().isEmpty()) {
            return schedule;
        }
        return scheduleString.toString();
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
                    String schedule = getString(R.string.services_schedule) + " " + formatSchedule(Objects.requireNonNull(service).getSchedule());
                    binding.cleaningService.txtServiceSchedule.setText(schedule);
                    if (service.getEmail() != null) {
                        binding.cleaningService.serviceEmail.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.cleaningService.serviceEmail.setVisibility(View.VISIBLE);
                        binding.cleaningService.txtServiceEmail.setText(service.getEmail());
                    } else {
                        binding.cleaningService.serviceEmail.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.cleaningService.serviceEmail.setVisibility(View.GONE);
                    }
                    if (service.getPhone() != null) {
                        binding.cleaningService.servicePhone.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.cleaningService.servicePhone.setVisibility(View.VISIBLE);
                        binding.cleaningService.txtServicePhone.setText(String.format(Locale.getDefault(), "%d", service.getPhone()));
                    } else {
                        binding.cleaningService.servicePhone.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.cleaningService.servicePhone.setVisibility(View.GONE);
                    }
                    if (service.getOccupation() != null) {
                        binding.cleaningService.serviceOccupation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.cleaningService.serviceOccupation.setVisibility(View.VISIBLE);
                        binding.cleaningService.txtServiceOccupation.setText(String.format(Locale.getDefault(), "%d", service.getOccupation()));
                    } else {
                        binding.cleaningService.serviceOccupation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.cleaningService.serviceOccupation.setVisibility(View.GONE);
                    }
                    if (service.getLocation() != null) {
                        binding.cleaningService.serviceLocation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.cleaningService.serviceLocation.setVisibility(View.VISIBLE);
                        binding.cleaningService.txtServiceLocation.setText(service.getLocation());
                    } else {
                        binding.cleaningService.serviceLocation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.cleaningService.serviceLocation.setVisibility(View.GONE);
                    }
                    // Get language code and set description
                    String languageCode = Locale.getDefault().getLanguage();
                    if (languageCode.equals("pt")) {
                        binding.cleaningService.txtDescription.setText(service.getDescription());
                    } else {
                        binding.cleaningService.txtDescription.setText(service.getDescriptionEN());
                    }
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