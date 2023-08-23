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
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentSpaBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.ServiceResponse;
import ipl.estg.happyguest.utils.models.Service;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import retrofit2.Call;
import retrofit2.Callback;

public class SpaFragment extends Fragment {

    private FragmentSpaBinding binding;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSpaBinding.inflate(inflater, container, false);

        // API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);
        HasCodes hasCodes = new HasCodes(binding.getRoot().getContext());

        // Check if user has codes and if not, redirect to home
        if (!hasCodes.getHasCode()) {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_home);
            }
        }

        // Close button listener
        binding.btnCloseService.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_home);
            }
        });

        getServiceAttempt();

        return binding.getRoot();
    }

    private String formatSchedule(String schedule) {
        // Schedule format is 9:00-14:00-15:00-21:00 or 9:00-23:00
        // Split the schedule into start and end times
        try {
            String[] scheduleArray = schedule.split("-");
            StringBuilder scheduleString = new StringBuilder();
            for (int i = 0; i < scheduleArray.length; i++) {
                if (i % 2 == 0) {
                    scheduleString.append(scheduleArray[i]).append("h-");
                } else {
                    scheduleString.append(scheduleArray[i]).append("h");
                    if (i != scheduleArray.length - 1) {
                        scheduleString.append(" ").append(getString(R.string.services_schedule_separator)).append(" ");
                    }
                }
            }
            if (scheduleString.toString().isEmpty()) {
                return schedule;
            }
            return scheduleString.toString();
        } catch (Exception e) {
            Log.e("populateFormatSchedule: ", Objects.requireNonNull(e.getMessage()));
            return schedule;
        }
    }

    private void getServiceAttempt() {
        Call<ServiceResponse> call = api.getService(5L);
        call.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServiceResponse> call, @NonNull retrofit2.Response<ServiceResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Service and populate fields
                    Service service = response.body().getService();
                    if (Objects.requireNonNull(service).isActive()) {
                        String schedule = getString(R.string.services_schedule) + " " + formatSchedule(Objects.requireNonNull(service).getSchedule());
                        binding.spaService.txtServiceSchedule.setText(schedule);
                        binding.spaService.txtServiceSchedule.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        binding.spaService.txtServiceSchedule.setText(R.string.services_unavailable);
                        binding.spaService.txtServiceSchedule.setTextColor(getResources().getColor(R.color.red, null));
                    }
                    if (service.getEmail() != null) {
                        binding.spaService.serviceEmail.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.spaService.serviceEmail.setVisibility(View.VISIBLE);
                        binding.spaService.txtServiceEmail.setText(service.getEmail());
                    } else {
                        binding.spaService.serviceEmail.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.spaService.serviceEmail.setVisibility(View.GONE);
                    }
                    if (service.getPhone() != null) {
                        binding.spaService.servicePhone.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.spaService.servicePhone.setVisibility(View.VISIBLE);
                        binding.spaService.txtServicePhone.setText(String.format(Locale.getDefault(), "%d", service.getPhone()));
                    } else {
                        binding.spaService.servicePhone.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.spaService.servicePhone.setVisibility(View.GONE);
                    }
                    if (service.getOccupation() != null) {
                        binding.spaService.serviceOccupation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.spaService.serviceOccupation.setVisibility(View.VISIBLE);
                        binding.spaService.txtServiceOccupation.setText(String.format(Locale.getDefault(), "%d", service.getOccupation()));
                    } else {
                        binding.spaService.serviceOccupation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.spaService.serviceOccupation.setVisibility(View.GONE);
                    }
                    if (service.getLocation() != null) {
                        binding.spaService.serviceLocation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.spaService.serviceLocation.setVisibility(View.VISIBLE);
                        binding.spaService.txtServiceLocation.setText(service.getLocation());
                    } else {
                        binding.spaService.serviceLocation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.spaService.serviceLocation.setVisibility(View.GONE);
                    }
                    // Get language code and set description
                    String languageCode = Locale.getDefault().getLanguage();
                    if (languageCode.equals("pt")) {
                        binding.spaService.txtDescription.setText(service.getDescription());
                    } else {
                        binding.spaService.txtDescription.setText(service.getDescriptionEN());
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
                Log.i("GetService Error: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}