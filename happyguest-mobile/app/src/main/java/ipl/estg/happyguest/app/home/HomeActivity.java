package ipl.estg.happyguest.app.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.LoginActivity;
import ipl.estg.happyguest.databinding.ActivityHomeBinding;
import ipl.estg.happyguest.utils.CloseService;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private APIRoutes api;
    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbar.toolbarMain);

        // Start CloseService
        Intent stickyService = new Intent(this, CloseService.class);
        startService(stickyService);

        // Set up navigation
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Hide title and back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // Open drawer
        binding.appBarHome.toolbar.btnBarOpen.setOnClickListener(v -> drawer.open());

        // Navigation listener
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Hide title and back button
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setDisplayShowTitleEnabled(false);
            }
            // Hide profile button
            ArrayList<Integer> navFragments = new ArrayList<>(Arrays.asList(R.id.nav_home));
            if (!navFragments.contains(destination.getId())) {
                binding.appBarHome.toolbar.btnBarProfile.setVisibility(View.INVISIBLE);
            } else {
                binding.appBarHome.toolbar.btnBarProfile.setVisibility(View.VISIBLE);
            }
            // Hide profile image
            if (destination.getId() == R.id.nav_profile) {
                binding.appBarHome.imageProfile.setVisibility(View.VISIBLE);
            } else {
                binding.appBarHome.imageProfile.setVisibility(View.INVISIBLE);
            }
        });

        // Go to home fragment
        binding.appBarHome.toolbar.btnBarLogo.setOnClickListener(v -> {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.nav_profile) {
                navController.navigate(R.id.action_profile_home);
            } else {
                navController.navigate(R.id.nav_home);
            }
            binding.appBarHome.toolbar.txBarTitle.setText(R.string.barTitle);
        });

        // Go to profile fragment
        binding.appBarHome.toolbar.btnBarProfile.setOnClickListener(v -> {
            navController.navigate(R.id.action_global_profile);
            binding.appBarHome.toolbar.txBarTitle.setText(R.string.barTitle_profile);
        });

        // API Routes and Token
        api = APIClient.getClient().create(APIRoutes.class);
        token = new Token(HomeActivity.this);

        // Buttons
        Button btnLogout = findViewById(R.id.btnLogout);

        // Attempt Logout
        btnLogout.setOnClickListener(v -> logoutAttempt());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isOpen()) {
            drawer.close();
        }
    }

    public void logoutAttempt() {
        Call<MessageResponse> call = api.logout();
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    // Delete token
                    token.clearToken();
                    APIClient.setToken(null);

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