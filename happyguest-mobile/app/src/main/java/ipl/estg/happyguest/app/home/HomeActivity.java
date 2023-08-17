package ipl.estg.happyguest.app.home;

import static ipl.estg.happyguest.utils.others.Images.getStreamByteFromImage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.LoginActivity;
import ipl.estg.happyguest.databinding.ActivityHomeBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.CheckOutRequest;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import ipl.estg.happyguest.utils.models.Code;
import ipl.estg.happyguest.utils.models.UserCode;
import ipl.estg.happyguest.utils.others.CircleImage;
import ipl.estg.happyguest.utils.others.CloseService;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    // Read QR Code
    public final ActivityResultLauncher<Intent> qrCodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                    String scannedString = scanResult.getContents();
                    if (scannedString != null && !scannedString.isEmpty()) {
                        TextInputLayout inputCode = findViewById(R.id.inputCode);
                        Objects.requireNonNull(inputCode.getEditText()).setText(scannedString);
                    }
                }
            }
    );
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private User user;
    private APIRoutes api;
    private Token token;
    private Button btnCheckOut;
    private Button btnLogout;
    private ArrayList<Code> codes;
    private byte[] photo;
    // Select Image from Gallery and convert to byte array
    private final ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            if (selectedImage == null) {
                                return;
                            }
                            InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                            // Create a temporary file to save the image
                            File tempFile = File.createTempFile("temp_image", null, getCacheDir());
                            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = Objects.requireNonNull(inputStream).read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, bytesRead);
                            }
                            // Close the streams
                            inputStream.close();
                            fileOutputStream.close();
                            photo = getStreamByteFromImage(tempFile);
                            Toast.makeText(this, R.string.save_photo, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbarMain);

        // Start CloseService
        Intent stickyService = new Intent(this, CloseService.class);
        startService(stickyService);

        // User, API and Token
        user = new User(binding.getRoot().getContext());
        token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        setupNavigation();

        // Resize title, logo and set profile image invisible
        binding.appBarHome.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            updateTitleAndLogoScale(percentage);
            updateProfileImageVisibility(percentage);
        });

        // Open drawer
        binding.appBarHome.btnBarOpen.setOnClickListener(v -> binding.drawerLayout.open());

        // Destination changed listener
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
            }

            // Set toolbar title and background
            if (destination.getId() == R.id.nav_home) {
                binding.appBarHome.txtBarTitle.setText(R.string.barTitle);
                binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_leiria2);
            } else {
                binding.appBarHome.txtBarTitle.setText(destination.getLabel());
                if (destination.getId() == R.id.nav_register_complaint || destination.getId() == R.id.nav_complaints || destination.getId() == R.id.nav_complaint) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_complaint);
                } else if (destination.getId() == R.id.nav_reviews || destination.getId() == R.id.nav_register_review || destination.getId() == R.id.nav_review) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_review);
                } else if (destination.getId() == R.id.nav_cleaning) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_clean);
                } else if (destination.getId() == R.id.nav_objects) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_objects);
                } else if (destination.getId() == R.id.nav_food) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_food);
                } else if (destination.getId() == R.id.nav_spa) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_spa);
                } else if (destination.getId() == R.id.nav_gym) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_gym);
                } else if (destination.getId() == R.id.nav_hotel) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_hotel);
                } else if (destination.getId() == R.id.nav_region) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_region);
                } else if (destination.getId() == R.id.nav_restaurant) {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_restaurant);
                } else if (destination.getId() == R.id.nav_codes) {
                    binding.appBarHome.txtBarTitle.setText(R.string.menu_reserve_code);
                } else {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_leiria2);
                }
            }

            // Set toolbar title lines
            if (binding.appBarHome.txtBarTitle.getText().toString().contains(" ")) {
                binding.appBarHome.txtBarTitle.setMinLines(2);
                binding.appBarHome.txtBarTitle.setMaxLines(2);
                binding.appBarHome.txtBarTitle.setMaxWidth(binding.appBarHome.txtBarTitle.getMinWidth());
            } else {
                binding.appBarHome.txtBarTitle.setMinLines(1);
                binding.appBarHome.txtBarTitle.setMaxLines(1);
                binding.appBarHome.txtBarTitle.setMaxWidth(100000);
            }

            // Set profile image
            if (destination.getId() == R.id.nav_profile || destination.getId() == R.id.nav_password) {
                binding.appBarHome.txtBarTitle.setGravity(Gravity.CENTER_HORIZONTAL);
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                binding.appBarHome.imageProfile.setVisibility(View.VISIBLE);
                binding.appBarHome.btnBarProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                binding.appBarHome.btnBarProfile.setVisibility(View.GONE);
            }
            if (destination.getId() != R.id.nav_profile && destination.getId() != R.id.nav_password && binding.appBarHome.imageProfile.getVisibility() == View.VISIBLE) {
                binding.appBarHome.txtBarTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_fast));
                binding.appBarHome.imageProfile.setVisibility(View.GONE);
                binding.appBarHome.btnBarProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                binding.appBarHome.btnBarProfile.setVisibility(View.VISIBLE);
                binding.appBarHome.btnBarProfile.setEnabled(true);
            }
            // Scroll to the top of the page
            AppBarLayout appBarLayout = findViewById(R.id.app_bar);
            appBarLayout.setExpanded(true);
            NestedScrollView nestedScrollView = findViewById(R.id.scrollView);
            nestedScrollView.smoothScrollTo(0, 0);
        });

        // Go to home fragment
        binding.appBarHome.btnBarLogo.setOnClickListener(v -> {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_home)
                return;
            navController.popBackStack();
            navController.navigate(R.id.action_nav_home);
        });

        // Go to profile fragment
        binding.appBarHome.btnBarProfile.setOnClickListener(v -> {
            binding.appBarHome.btnBarProfile.setEnabled(false);
            navController.popBackStack();
            navController.navigate(R.id.action_nav_profile);

        });

        // Button Check-Out
        btnCheckOut = findViewById(R.id.btnCheckout);
        btnCheckOut.setOnClickListener(v -> {
            // Check if user has already reviewed the app in the last 7 days
            if (user.getLastReview() != null) {
                try {
                    DateFormat dateFormat = DateFormat.getDateInstance();
                    Date lastReview = dateFormat.parse(user.getLastReview());
                    if (lastReview != null && System.currentTimeMillis() - lastReview.getTime() < 7 * 24 * 60 * 60 * 1000) {
                        showPopupCheckout();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                showPopupNoReview();
            }
        });

        // Button logout
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            if (btnLogout.isEnabled()) logoutAttempt();
        });

        // Get user data if it's not already loaded
        if (user.getName() == null) {
            getMeAttempt();
        } else if (user.getPhotoUrl() != null) {
            populateImageProfile();
        }

        // Select Image
        binding.appBarHome.imageProfile.setOnClickListener(v -> {
            if (editMode) {
                binding.appBarHome.imageUpload.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_fast));
                binding.appBarHome.imageUpload.setVisibility(View.VISIBLE);
                Intent photoPicker = new Intent();
                photoPicker.setType("image/*");
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                startActivityResult.launch(Intent.createChooser(photoPicker, getString(R.string.select_image)));
                new Handler().postDelayed(() -> {
                    binding.appBarHome.imageUpload.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_fast));
                    binding.appBarHome.imageUpload.setVisibility(View.GONE);
                }, 1000);
            }
        });
    }

    private void showPopupNoReview() {
        LayoutInflater inflater = (LayoutInflater) binding.getRoot().getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup, null);

        // Create the popup window
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set background color
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));

        // Set popup texts
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.title_no_review));

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        LinearLayout layout = popupView.findViewById(R.id.buttonsPopUp);
        layout.setVisibility(View.GONE);

        LinearLayout layout2 = popupView.findViewById(R.id.buttonsReview);
        layout2.setVisibility(View.VISIBLE);

        // No popup
        Button btnNo = popupView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            showPopupCheckout();
        });

        // Yes popup
        Button btnYes = popupView.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            binding.drawerLayout.close();
            changeFragment(R.id.action_nav_register_review);
        });
    }

    private void showPopupCheckout() {
        LayoutInflater inflater = (LayoutInflater) binding.getRoot().getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup, null);

        // Create the popup window
        int width = RelativeLayout.LayoutParams.MATCH_PARENT;
        int height = RelativeLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set background color
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));

        // Set popup texts
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.title_checkOut));
        TextView description = popupView.findViewById(R.id.txtRegionDescription);
        description.setVisibility(View.VISIBLE);
        description.setText(getString(R.string.description_checkOut));
        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        Spinner spinner = popupView.findViewById(R.id.spinnerCode);
        spinner.setVisibility(View.VISIBLE);
        getCodesAttempt(spinner);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setOnClickListener(view1 -> {
            if (spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.select_code), Toast.LENGTH_SHORT).show();
                return;
            } else {
                checkOutAttempt(codes.get(spinner.getSelectedItemPosition() - 1).getId());
            }
            binding.drawerLayout.close();
            popupWindow.dismiss();
        });
    }

    private void checkOutAttempt(Long code) {
        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        Call<MessageResponse> call = api.checkOut(new CheckOutRequest(user.getId(), code, date));
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and change fragment
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                Toast.makeText(binding.getRoot().getContext(), errors.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                                // If no more codes available, update fragment
                                if (codes.size() == 1) {
                                    changeFragment(R.id.action_nav_home);
                                    HasCodes hasCodes = new HasCodes(getApplicationContext());
                                    hasCodes.setHasCode(false, "");
                                    homeWithCodes(false);
                                }
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("CheckOut Error: ", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("CheckOut Error: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void getCodesAttempt(Spinner spinner) {
        Call<CodesResponse> call = api.getUserCodes(user.getId(), 1, "V");
        call.enqueue(new Callback<CodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<CodesResponse> call, @NonNull Response<CodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    codes = new ArrayList<>();
                    ArrayList<UserCode> userCodes = response.body().getData();
                    for (UserCode userCode : userCodes) {
                        codes.add(userCode.getCode());
                    }
                    ArrayAdapter<Code> adapter = new ArrayAdapter<>(binding.getRoot().getContext(), android.R.layout.simple_spinner_item, codes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.codes_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetCodes Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CodesResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.codes_error), Toast.LENGTH_SHORT).show();
                Log.i("GetCodes Error: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isOpen()) {
            drawer.close();
        }
    }

    public void homeWithCodes(boolean hasCode) {
        if (btnCheckOut != null) {
            if (hasCode) {
                btnCheckOut.setVisibility(View.VISIBLE);
            } else {
                btnCheckOut.setVisibility(View.GONE);
            }
        }
    }

    public void openWebsite(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void changeFragment(int id) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        navController.popBackStack();
        navController.navigate(id);
    }

    public void changeFragmentService(int id, Long idObject, Long position) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", idObject);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        navController.popBackStack();
        navController.navigate(id, bundle);
        new Handler().postDelayed(() -> {
            String title = binding.appBarHome.txtBarTitle.getText() + position.toString();
            binding.appBarHome.txtBarTitle.setText(title);
        }, 100);
    }

    public void changeFragmentFilter(int id, String filter) {
        Bundle bundle = new Bundle();
        bundle.putString("filter", filter);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        navController.popBackStack();
        navController.navigate(id, bundle);
    }

    public void changeFragmentService(int id, Long idObject, char type) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", idObject);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        navController.popBackStack();
        navController.navigate(id, bundle);
        switch (type) {
            case 'C':
                binding.appBarHome.txtBarTitle.setText(R.string.menu_cleaning);
                binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_clean);
                break;
            case 'B':
                binding.appBarHome.txtBarTitle.setText(R.string.menu_objects);
                binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_objects);
                break;
            case 'F':
                binding.appBarHome.txtBarTitle.setText(R.string.menu_food);
                binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_food);
                break;
            case 'R':
                binding.appBarHome.txtBarTitle.setText(R.string.menu_table_reservation);
                binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_restaurant);
                break;
            case 'O':
                binding.appBarHome.txtBarTitle.setText(R.string.menu_reserve);
                break;
        }

        // Set toolbar title lines
        if (binding.appBarHome.txtBarTitle.getText().toString().contains(" ")) {
            binding.appBarHome.txtBarTitle.setMinLines(2);
            binding.appBarHome.txtBarTitle.setMaxLines(2);
            binding.appBarHome.txtBarTitle.setMaxWidth(binding.appBarHome.txtBarTitle.getMinWidth());
        } else {
            binding.appBarHome.txtBarTitle.setMinLines(1);
            binding.appBarHome.txtBarTitle.setMaxLines(1);
            binding.appBarHome.txtBarTitle.setMaxWidth(100000);
        }
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setEditMode(Boolean editMode) {
        this.editMode = editMode;
    }

    private void setupNavigation() {
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_profile, R.id.nav_password, R.id.nav_reviews,
                R.id.nav_register_review, R.id.nav_complaints, R.id.nav_codes, R.id.nav_complaint, R.id.nav_review, R.id.nav_register_complaint,
                R.id.nav_cleaning, R.id.nav_objects, R.id.nav_food, R.id.nav_orders, R.id.nav_order, R.id.nav_gym, R.id.nav_spa, R.id.nav_restaurant,
                R.id.nav_reserves, R.id.nav_reserve, R.id.nav_hotel, R.id.nav_region)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void updateTitleAndLogoScale(float percentage) {
        float titleScale = 1.0f - (percentage * 0.5f);
        binding.appBarHome.txtBarTitle.post(() -> {
            binding.appBarHome.txtBarTitle.setScaleX(titleScale);
            binding.appBarHome.txtBarTitle.setScaleY(titleScale);
            if (percentage > 0.95f) {
                binding.appBarHome.txtBarTitle.setMinLines(1);
            } else {
                binding.appBarHome.txtBarTitle.setMinLines(2);
            }
        });

        float logoScale = 0.8f - (percentage * 0.1f);
        binding.appBarHome.btnBarLogo.setScaleX(logoScale);
        binding.appBarHome.btnBarLogo.setScaleY(logoScale - 0.1f);
    }

    private void updateProfileImageVisibility(float percentage) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        if (currentDestinationId == R.id.nav_profile || currentDestinationId == R.id.nav_password) {
            if (binding.appBarHome.imageProfile.getVisibility() == View.VISIBLE && percentage > 0.05f) {
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_fast));
                binding.appBarHome.imageProfile.setVisibility(View.GONE);
            } else if (binding.appBarHome.imageProfile.getVisibility() == View.GONE && percentage < 0.05f) {
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_fast));
                binding.appBarHome.imageProfile.setVisibility(View.VISIBLE);
            }
        }
    }

    public void populateImageProfile() {
        if (user.getPhotoUrl() != null) {
            Picasso.get().load(getString(R.string.api_photos) + "storage/user_photos/" + user.getPhotoUrl()).transform(new CircleImage()).into(binding.appBarHome.imageProfile);
        } else {
            binding.appBarHome.imageProfile.setImageResource(R.drawable.profile_icon);
        }
    }

    private void getMeAttempt() {
        // Get locale
        String languageCode = Locale.getDefault().getLanguage();
        Call<UserResponse> call = api.me(languageCode);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save user data
                    user.setUser(response.body().getId(), response.body().getName(), response.body().getEmail(), response.body().getPhone() == null ? -1 : response.body().getPhone(), response.body().getAddress(),
                            response.body().getBirthDate(), response.body().getPhotoUrl(), response.body().getLastReview());
                    if (user.getPhotoUrl() != null) {
                        populateImageProfile();
                    }
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetMe Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                Log.i("GetMe Error: ", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public void logoutAttempt() {
        btnLogout.setEnabled(false);
        Call<MessageResponse> call = api.logout();
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    // Delete token
                    token.clearToken();
                    user.clearUser();

                    // Display success message and go to HomeActivity
                    Toast.makeText(HomeActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                    finish();
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.logout_error), Toast.LENGTH_SHORT).show();
                    Log.i("Logout Error: ", response.message());
                    btnLogout.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, getString(R.string.logout_error), Toast.LENGTH_SHORT).show();
                Log.i("Logout Error: ", Objects.requireNonNull(t.getMessage()));
                btnLogout.setEnabled(true);
            }
        });
    }
}