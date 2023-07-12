package ipl.estg.happyguest.app.home.services;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentObjectsBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.OrderRequest;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.ServiceResponse;
import ipl.estg.happyguest.utils.models.Item;
import ipl.estg.happyguest.utils.models.OrderItem;
import ipl.estg.happyguest.utils.models.Service;
import ipl.estg.happyguest.utils.models.UserCode;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectsFragment extends Fragment {

    private FragmentObjectsBinding binding;
    private APIRoutes api;
    private User user;
    private String selectedRoom;
    private String selectedFilter = "All";
    private String menuURL;
    private ItemsAdapter itemsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentObjectsBinding.inflate(inflater, container, false);

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

        // Register button listener
        binding.btnOrderObjects.setOnClickListener(v -> {
            if (selectedRoom == null) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.room_required), Toast.LENGTH_SHORT).show();
            } else if (itemsAdapter.getOrderItems().isEmpty()) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.items_required), Toast.LENGTH_SHORT).show();
            } else {
                showPopup();
            }
        });

        // History button listener
        binding.btnHistoryObjects.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action_nav_orders);
            }
        });

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

        // Spinner category listener
        binding.spinnerItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getItemAtPosition(position).toString();
                if (itemsAdapter != null) {
                    itemsAdapter.setFilter(selectedFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFilter = null;
                if (itemsAdapter != null) {
                    itemsAdapter.setFilter("All");
                }
            }
        });

        // Open menu button listener
        binding.objectsService.btnMenu.setOnClickListener(v -> {
            if (menuURL != null) {
                String menu = getString(R.string.api_photos) + "storage/services/" + menuURL;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(menu));
                startActivity(intent);
            }
        });

        return binding.getRoot();
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
        popupView.findViewById(R.id.dividerOrder).setVisibility(View.VISIBLE);
        popupView.findViewById(R.id.txtPopUpTotal).setVisibility(View.VISIBLE);
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.service_objects_confirm));
        StringBuilder sb = new StringBuilder(getString(R.string.services_room) + " " + selectedRoom);
        ((TextView) popupView.findViewById(R.id.txtPopUpRoom)).setText(sb.toString());
        sb = new StringBuilder();
        for (OrderItem item : itemsAdapter.getOrderItems()) {
            sb.append(item.getQuantity()).append("x ").append(item.getName());
            if (itemsAdapter.getOrderItems().indexOf(item) != itemsAdapter.getOrderItems().size() - 1) {
                sb.append("\n");
            }
        }
        ((TextView) popupView.findViewById(R.id.txtOrder)).setText(sb.toString());
        sb = new StringBuilder();
        sb.append(getString(R.string.total_price)).append(" ").append(itemsAdapter.getTotalPrice()).append("€");
        ((TextView) popupView.findViewById(R.id.txtPopUpTotal)).setText(sb.toString());

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
            binding.btnOrderObjects.setEnabled(false);
            binding.btnHistoryObjects.setEnabled(false);
            popupWindow.dismiss();
        });
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
            Log.e("populateFormatSchedule: ", e.getMessage());
            return schedule;
        }
    }

    private void populateMenu(ArrayList<Item> items) {
        // Create adapter and set it to recycler view
        itemsAdapter = new ItemsAdapter(items, binding.getRoot().getContext(), binding.txtPrice, binding.txtNoItems);
        binding.itemsRV.setAdapter(itemsAdapter);
        binding.itemsRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
    }

    private void getServiceAttempt() {
        Call<ServiceResponse> call = api.getService(2L);
        call.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServiceResponse> call, @NonNull retrofit2.Response<ServiceResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    // Get Service and populate fields
                    Service service = response.body().getService();
                    if (Objects.requireNonNull(service).isActive()) {
                        binding.btnOrderObjects.setEnabled(true);
                        binding.spinnerRoom.setEnabled(true);
                        binding.inputComment.setEnabled(true);
                        String schedule = getString(R.string.services_schedule) + " " + formatSchedule(Objects.requireNonNull(service).getSchedule());
                        binding.objectsService.txtServiceSchedule.setText(schedule);
                        binding.objectsService.txtServiceSchedule.setTextColor(getResources().getColor(R.color.black, null));
                    } else {
                        binding.btnOrderObjects.setEnabled(false);
                        binding.spinnerRoom.setEnabled(false);
                        binding.inputComment.setEnabled(false);
                        binding.objectsService.txtServiceSchedule.setText(R.string.services_unavailable);
                        binding.objectsService.txtServiceSchedule.setTextColor(getResources().getColor(R.color.red, null));
                    }
                    if (service.getEmail() != null) {
                        binding.objectsService.serviceEmail.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.objectsService.serviceEmail.setVisibility(View.VISIBLE);
                        binding.objectsService.txtServiceEmail.setText(service.getEmail());
                    } else {
                        binding.objectsService.serviceEmail.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.objectsService.serviceEmail.setVisibility(View.GONE);
                    }
                    if (service.getPhone() != null) {
                        binding.objectsService.servicePhone.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.objectsService.servicePhone.setVisibility(View.VISIBLE);
                        binding.objectsService.txtServicePhone.setText(String.format(Locale.getDefault(), "%d", service.getPhone()));
                    } else {
                        binding.objectsService.servicePhone.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.objectsService.servicePhone.setVisibility(View.GONE);
                    }
                    if (service.getOccupation() != null) {
                        binding.objectsService.serviceOccupation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.objectsService.serviceOccupation.setVisibility(View.VISIBLE);
                        binding.objectsService.txtServiceOccupation.setText(String.format(Locale.getDefault(), "%d", service.getOccupation()));
                    } else {
                        binding.objectsService.serviceOccupation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.objectsService.serviceOccupation.setVisibility(View.GONE);
                    }
                    if (service.getLocation() != null) {
                        binding.objectsService.serviceLocation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in_fast));
                        binding.objectsService.serviceLocation.setVisibility(View.VISIBLE);
                        binding.objectsService.txtServiceLocation.setText(service.getLocation());
                    } else {
                        binding.objectsService.serviceLocation.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out_fast));
                        binding.objectsService.serviceLocation.setVisibility(View.GONE);
                    }
                    // Get language code and set description
                    String languageCode = Locale.getDefault().getLanguage();
                    if (languageCode.equals("pt")) {
                        binding.objectsService.txtDescription.setText(service.getDescription());
                    } else {
                        binding.objectsService.txtDescription.setText(service.getDescriptionEN());
                    }
                    if (service.getMenu_url() != null) {
                        binding.objectsService.btnMenu.setVisibility(View.VISIBLE);
                        menuURL = service.getMenu_url();
                    }
                    // Get items
                    if (service.getItems() != null) {
                        populateMenu(service.getItems());
                    }
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
                            // Get rooms
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
                        binding.btnOrderObjects.setEnabled(true);
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

    private void registerOrderAttempt() {
        String comment = Objects.requireNonNull(binding.txtComment.getText()).toString().isEmpty() ? null : binding.txtComment.getText().toString();
        Call<MessageResponse> call = api.registerOrder(new OrderRequest(user.getId(), selectedRoom, null, 2L, itemsAdapter.getOrderItems(), itemsAdapter.getTotalPrice(), comment));
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                binding.btnOrderObjects.setEnabled(true);
                binding.btnHistoryObjects.setEnabled(true);
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
                        Log.i("RegisterOrder Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("RegisterOrder Error: ", t.getMessage());
                binding.btnOrderObjects.setEnabled(true);
                binding.btnHistoryObjects.setEnabled(true);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}