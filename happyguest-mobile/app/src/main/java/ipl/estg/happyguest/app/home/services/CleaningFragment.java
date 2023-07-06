package ipl.estg.happyguest.app.home.services;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentCleaningBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.ServiceResponse;
import ipl.estg.happyguest.utils.models.Service;
import ipl.estg.happyguest.utils.models.UserCode;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CleaningFragment extends Fragment {

    private FragmentCleaningBinding binding;
    private APIRoutes api;
    private User user;
    private String selectedRoom;
    private String selectedSchedule;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCleaningBinding.inflate(inflater, container, false);

        // User, API and Token
        user = new User(binding.getRoot().getContext());
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

        getServiceAttempt();
        getCodesAttempt();

        // Spinner room listener
        binding.spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoom = null;
            }
        });

        // Spinner schedule listener
        binding.spinnerSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSchedule = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSchedule = null;
            }
        });

        return binding.getRoot();
    }

    private String formatSchedule(String schedule) {
        // Schedule format is 9:00-14:00-15:00-21:00 or 9:00-23:00
        // We want 9:00h-14:00h and 15:00h-21:00h
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
    }

    private void populateScheduleSpinner(String schedule) {
        // Schedule format is 9:00-14:00-15:00-21:00 or 9:00-23:00
        // We want 15 minute intervals respecting the schedule

        // TODO: Generate schedule times in 15-minute intervals for the specified number of hours (schedule) HH:mm

        List<String> availableDates = new ArrayList<>();

        // Set the start time to the next nearest 15-minute mark
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
        calendar.add(Calendar.MINUTE, (15 - calendar.get(Calendar.MINUTE) % 15) % 15);
        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));
        String startTime = dateFormat.format(calendar.getTime());
        availableDates.add(startTime);

        // Generate dates in 15-minute intervals for the specified number of hours
        for (int i = 0; i < 24 * 4; i++) {
            calendar.add(Calendar.MINUTE, 15);
            String date = dateFormat.format(calendar.getTime());

            // TODO: If date is in the generated schedule times, add it to the spinner
            // availableDates.add(date);

            if (calendar.get(Calendar.DATE) - 1 > Calendar.getInstance().get(Calendar.DATE)) {
                break; // Stop after 24 hours
            }
        }

        // Create adapter and set it to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, availableDates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSchedule.setAdapter(adapter);
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
                    populateScheduleSpinner(service.getSchedule());
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

    private void getCodesAttempt() {
        Call<CodesResponse> call = api.getUserCodes(user.getId(), 0, "V");
        call.enqueue(new Callback<CodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<CodesResponse> call, @NonNull Response<CodesResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Save codes in list and populate spinner
                    ArrayList<UserCode> userCodes = new ArrayList<>(response.body().getData() != null ? response.body().getData() : new ArrayList<>());
                    if (userCodes.size() > 0) {
                        // Get rooms from codes
                        ArrayList<String> rooms = new ArrayList<>();
                        for (UserCode userCode : userCodes) {
                            for (String room : userCode.getCode().getRooms()) {
                                if (!rooms.contains(room)) {
                                    rooms.add(room);
                                }
                            }
                        }
                        // Create adapter and set it to spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, rooms);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinnerRoom.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.codes_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetCodes Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CodesResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.codes_error), Toast.LENGTH_SHORT).show();
                Log.i("GetCodes Error: ", t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}