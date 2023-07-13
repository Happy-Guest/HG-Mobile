package ipl.estg.happyguest.app.home;

import static ipl.estg.happyguest.utils.others.Images.getStreamByteFromImage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.LoginActivity;
import ipl.estg.happyguest.databinding.ActivityHomeBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import ipl.estg.happyguest.utils.others.CircleImage;
import ipl.estg.happyguest.utils.others.CloseService;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private User user;
    private APIRoutes api;
    private Token token;
    private Button btnLogout;
    private int titleMaxWidth;
    private byte[] photo;
    // Select Image from Gallery and convert to byte array
    private final ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                            // Create a temporary file to save the image
                            File tempFile = File.createTempFile("temp_image", null, getCacheDir());
                            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
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

        // Title max width
        titleMaxWidth = binding.appBarHome.txtBarTitle.getMaxWidth();

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
                } else if (destination.getId() == R.id.nav_codes) {
                    binding.appBarHome.txtBarTitle.setText(R.string.menu_reserve_code);
                } else if (destination.getId() == R.id.nav_hotel){
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_hotel);
                }
                else {
                    binding.appBarHome.toolbarLayout.setBackgroundResource(R.drawable.bg_leiria2);
                }
            }

            // Check title size
            if (binding.appBarHome.txtBarTitle.getText().toString().contains(" ")) {
                binding.appBarHome.txtBarTitle.setMaxWidth(titleMaxWidth);
            } else {
                binding.appBarHome.txtBarTitle.setMaxWidth(0);
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        List<Integer> menuItemIds = Collections.emptyList(); // TODO: Add check-out menu item
        // Show or hide multiple menu items
        for (int menuItemId : menuItemIds) {
            MenuItem menuItem = navigationView.getMenu().findItem(menuItemId);
            menuItem.setVisible(hasCode);
        }
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
                R.id.nav_cleaning, R.id.nav_objects, R.id.nav_food, R.id.nav_orders, R.id.nav_order, R.id.nav_gym, R.id.nav_spa, R.id.nav_reserves, R.id.nav_reserve ,R.id.nav_hotel)
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
                Log.i("GetMe Error: ", t.getMessage());
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
                Log.i("Logout Error: ", t.getMessage());
                btnLogout.setEnabled(true);
            }
        });
    }

}