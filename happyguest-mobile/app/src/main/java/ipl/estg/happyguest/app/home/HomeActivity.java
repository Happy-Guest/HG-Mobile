package ipl.estg.happyguest.app.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.LoginActivity;
import ipl.estg.happyguest.databinding.ActivityHomeBinding;
import ipl.estg.happyguest.utils.CircleImage;
import ipl.estg.happyguest.utils.CloseService;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.User;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbarMain);

        // Start CloseService
        Intent stickyService = new Intent(this, CloseService.class);
        startService(stickyService);

        // User
        user = new User(getApplicationContext());

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

        // Hide old icon and check profile image
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
            }
            if (destination.getId() == R.id.nav_profile) {
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                binding.appBarHome.imageProfile.setVisibility(View.VISIBLE);
                binding.appBarHome.btnBarProfile.setVisibility(View.INVISIBLE);
            } else {
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_fast));
                binding.appBarHome.imageProfile.setVisibility(View.INVISIBLE);
                binding.appBarHome.btnBarProfile.setVisibility(View.VISIBLE);
            }
        });

        // Go to home fragment
        binding.appBarHome.btnBarLogo.setOnClickListener(v -> {
            int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
            if (currentDestinationId == R.id.nav_profile) {
                navController.navigate(R.id.action_profile_home);
            } else {
                navController.navigate(R.id.nav_home);
            }
            binding.appBarHome.txtBarTitle.setText(R.string.barTitle);
        });

        // Go to profile fragment
        binding.appBarHome.btnBarProfile.setOnClickListener(v -> {
            navController.navigate(R.id.action_global_profile);
            binding.appBarHome.txtBarTitle.setText(R.string.barTitle_profile);
        });

        // Button logout
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logoutAttempt());

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

    private void setupNavigation() {
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
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
        if (currentDestinationId == R.id.nav_profile) {
            if (binding.appBarHome.imageProfile.getVisibility() == View.VISIBLE && percentage > 0.05f) {
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_fast));
                binding.appBarHome.imageProfile.setVisibility(View.INVISIBLE);
            } else if (binding.appBarHome.imageProfile.getVisibility() == View.INVISIBLE && percentage < 0.05f) {
                binding.appBarHome.imageProfile.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_fast));
                binding.appBarHome.imageProfile.setVisibility(View.VISIBLE);
            }
        }
    }

    public void populateImageProfile() {
        Picasso.get().load(getString(R.string.api_photos) + "storage/user_photos/" + user.getPhotoUrl()).transform(new CircleImage()).into(binding.appBarHome.imageProfile);
    }

    public void logoutAttempt() {
        Token token = new Token(HomeActivity.this);
        APIRoutes api = APIClient.getClient(token.getToken()).create(APIRoutes.class);
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
                    Toast.makeText(HomeActivity.this, getString(R.string.logout_error), Toast.LENGTH_LONG).show();
                    Log.i("Logout Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, getString(R.string.logout_error), Toast.LENGTH_LONG).show();
                Log.i("Logout Error: ", t.getMessage());
                call.cancel();
            }
        });
    }
}