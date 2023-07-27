package ipl.estg.happyguest.app.home.services;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import ipl.estg.happyguest.utils.api.requests.OrderRequest;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.ServiceResponse;
import ipl.estg.happyguest.utils.models.Code;
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
    private Code selectedCode;
    private String selectedRoom;
    private String selectedSchedule;
    private String schedule;
    private ArrayList<UserCode> userCodes = new ArrayList<>();
    private ArrayList<Code> roomsCode = new ArrayList<>();

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

        binding.spinnerSchedule.setEnabled(false);
        getServiceAttempt();

        // Register button listener
        binding.btnOrderCleaning.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.room_required), Toast.LENGTH_SHORT).show();
            } else if (selectedSchedule == null) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.schedule_required), Toast.LENGTH_SHORT).show();
            } else {
                showPopup();
            }
        });

        // History button listener
        binding.btnHistoryCleaning.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragmentFilter(R.id.action_nav_orders, "OC");
            }
        });

        // Spinner room listener
        binding.spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom = parent.getItemAtPosition(position).toString();
                selectedCode = roomsCode.get(position);
                binding.spinnerSchedule.setEnabled(true);
                populateScheduleSpinner(schedule);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRoom = null;
                selectedCode = null;
                binding.spinnerSchedule.setEnabled(false);
            }
        });

        // Spinner schedule listener
        binding.spinnerSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(getString(R.string.services_schedule_tomorrow) + ":")) {
                    selectedSchedule = null;
                    int nextPosition = position + 1;
                    if (nextPosition < parent.getCount()) {
                        binding.spinnerSchedule.setSelection(nextPosition);
                    }
                } else {
                    selectedSchedule = parent.getItemAtPosition(position).toString();
                }
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

    private void populateScheduleSpinner(String schedule) {
        // Schedule format is 9:00-14:00-15:00-21:00 or 9:30-23:00
        List<String> availableDates = new ArrayList<>();
        List<String> scheduleDates = new ArrayList<>();

        try {
            // Split the schedule into start and end times
            String[] scheduleArray = schedule.split("-");
            for (int i = 0; i < scheduleArray.length; i += 2) {
                // Get the start and end times
                String[] scheduleStartTime = scheduleArray[i].split(":");
                int hour = Integer.parseInt(scheduleStartTime[0]);
                int minute = Integer.parseInt(scheduleStartTime[1]);
                String[] scheduleEndTime = scheduleArray[i + 1].split(":");
                int endHour = Integer.parseInt(scheduleEndTime[0]);
                int endMinute = Integer.parseInt(scheduleEndTime[1]);

                // Get the start time
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));

                // Get all the dates between the start and end times in 15-minute intervals
                for (; hour <= endHour; hour++) {
                    for (; minute < 60; minute += 15) {
                        if (hour == endHour && minute > endMinute) {
                            break;
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        scheduleDates.add(dateFormat.format(calendar.getTime()));
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        scheduleDates.add(dateFormat.format(calendar.getTime()));
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                    }
                    minute = 0;
                }
            }

            // Set the start time to the next nearest 15-minute mark
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
            calendar.add(Calendar.MINUTE, (15 - calendar.get(Calendar.MINUTE) % 15) % 15);
            calendar.set(Calendar.SECOND, 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));

            Calendar codeCalendar = Calendar.getInstance();
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateFormat2.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));
            codeCalendar.setTime(Objects.requireNonNull(dateFormat2.parse(selectedCode.getExitDate())));
            codeCalendar.set(Calendar.HOUR_OF_DAY, 0);
            codeCalendar.set(Calendar.MINUTE, 0);
            codeCalendar.set(Calendar.SECOND, 0);
            codeCalendar.set(Calendar.MILLISECOND, 0);

            // Generate dates in 15-minute intervals for the specified number of hours
            boolean tomorrow = false;
            for (int i = 0; i < 24 * 4; i++) {
                String date = dateFormat.format(calendar.getTime());
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(Objects.requireNonNull(dateFormat.parse(date)));
                dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                dateCalendar.set(Calendar.MINUTE, 0);
                dateCalendar.set(Calendar.SECOND, 0);
                dateCalendar.set(Calendar.MILLISECOND, 0);
                if (dateCalendar.after(codeCalendar)) {
                    break;
                }
                // Check if date is in the schedule
                if (scheduleDates.contains(date) && !availableDates.contains(date)) {
                    if (calendar.get(Calendar.DATE) - 1 == Calendar.getInstance().get(Calendar.DATE) && !tomorrow) {
                        tomorrow = true;
                        availableDates.add(getString(R.string.services_schedule_tomorrow) + ":");
                    }
                    availableDates.add((date));
                }
                calendar.add(Calendar.MINUTE, 15);
                if (calendar.get(Calendar.DATE) - 1 > Calendar.getInstance().get(Calendar.DATE)) {
                    break; // Stop after 24 hours
                }
            }

            // Create adapter and set it to spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, availableDates);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerSchedule.setAdapter(adapter);
        } catch (Exception e) {
            Log.i("populateScheduleSpinner: ", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
            selectedSchedule = null;
        }

        if (availableDates.isEmpty()) {
            binding.spinnerSchedule.setEnabled(false);
            binding.btnOrderCleaning.setEnabled(false);
            Toast.makeText(binding.getRoot().getContext(), getString(R.string.services_schedule_unavailable), Toast.LENGTH_SHORT).show();
        } else {
            binding.spinnerSchedule.setEnabled(true);
            binding.btnOrderCleaning.setEnabled(true);
        }
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
                    if (Objects.requireNonNull(service).isActive()) {
                        binding.btnOrderCleaning.setEnabled(true);
                        binding.spinnerRoom.setEnabled(true);
                        binding.inputComment.setEnabled(true);
                        String schedule = getString(R.string.services_schedule) + " " + formatSchedule(Objects.requireNonNull(service).getSchedule());
                        binding.cleaningService.txtServiceSchedule.setText(schedule);
                        binding.cleaningService.txtServiceSchedule.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        binding.btnOrderCleaning.setEnabled(false);
                        binding.spinnerRoom.setEnabled(false);
                        binding.inputComment.setEnabled(false);
                        binding.cleaningService.txtServiceSchedule.setText(R.string.services_unavailable);
                        binding.cleaningService.txtServiceSchedule.setTextColor(getResources().getColor(R.color.red, null));
                    }
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
                    schedule = service.getSchedule();
                    getCodesAttempt();
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

    private void getCodesAttempt() {
        Call<CodesResponse> call = api.getUserCodes(user.getId(), 0, "V");
        call.enqueue(new Callback<CodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<CodesResponse> call, @NonNull Response<CodesResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Save codes in list and populate spinner
                    userCodes = new ArrayList<>(response.body().getData() != null ? response.body().getData() : new ArrayList<>());
                    if (userCodes.size() > 0) {
                        // Get rooms from codes
                        ArrayList<String> rooms = new ArrayList<>();
                        roomsCode = new ArrayList<>();
                        for (UserCode userCode : userCodes) {
                            // Get rooms
                            for (String room : userCode.getCode().getRooms()) {
                                if (!rooms.contains(room)) {
                                    rooms.add(room);
                                    roomsCode.add(userCode.getCode());
                                }
                            }
                        }
                        // Create adapter and set it to spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, rooms);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinnerRoom.setAdapter(adapter);
                        binding.btnOrderCleaning.setEnabled(true);
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
                Log.i("GetCodes Error: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup, null);

        // Create the popup window
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set background color
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));

        // Set popup texts
        popupView.findViewById(R.id.txtPopUpRoom).setVisibility(View.VISIBLE);
        popupView.findViewById(R.id.txtOrder).setVisibility(View.VISIBLE);
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.service_clean_confirm));
        String sb = getString(R.string.services_room) + " " + selectedRoom;
        ((TextView) popupView.findViewById(R.id.txtPopUpRoom)).setText(sb);
        sb = getString(R.string.services_schedule) + " " + selectedSchedule;
        ((TextView) popupView.findViewById(R.id.txtOrder)).setText(sb);

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setOnClickListener(view1 -> {
            registerOrderAttempt();
            binding.btnOrderCleaning.setEnabled(false);
            binding.btnHistoryCleaning.setEnabled(false);
            popupWindow.dismiss();
        });
    }

    private String formatDate(String date) {
        // Format: dd/MM/yyyy HH:mm -> yyyy/MM/dd HH:mm
        String[] dateParts = date.split(" ");
        if (dateParts.length == 2 && dateParts[0].contains("/")) {
            String[] dayParts = dateParts[0].split("/");
            if (dayParts.length == 3) {
                return dayParts[2] + "/" + dayParts[1] + "/" + dayParts[0] + " " + dateParts[1];
            }
        }
        return date;
    }

    private void registerOrderAttempt() {
        String comment = Objects.requireNonNull(binding.txtComment.getText()).toString().isEmpty() ? null : binding.txtComment.getText().toString();
        Call<MessageResponse> call = api.registerOrder(new OrderRequest(user.getId(), selectedRoom, formatDate(selectedSchedule), 1L, null, null, comment));
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                binding.btnOrderCleaning.setEnabled(true);
                binding.btnHistoryCleaning.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and change fragment
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.changeFragment(R.id.action_nav_orders);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                if (errors.has("comment")) {
                                    binding.inputComment.setError(errors.getJSONArray("comment").get(0).toString());
                                } else {
                                    binding.inputComment.setError(null);
                                    Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("RegisterOrder Error: ", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("RegisterOrder Error: ", Objects.requireNonNull(t.getMessage()));
                binding.btnOrderCleaning.setEnabled(true);
                binding.btnHistoryCleaning.setEnabled(true);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}