package ipl.estg.happyguest.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.LoginActivity;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import ipl.estg.happyguest.utils.others.Token;
import ipl.estg.happyguest.utils.others.User;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private Token token;
    private User me;
    private APIRoutes api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Token, User and APIRoutes
        token = new Token(this);
        me = new User(this);
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Check if user is logged in and if so, get his data
        if (token.getRemember() && !token.getToken().isEmpty()) {
            getMeAttempt();
        } else {
            // If user is not logged in, redirect to login page
            redirectToLogin();
        }
    }

    private void getMeAttempt() {
        Call<UserResponse> call = api.me();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message, save user data and redirect to home page
                    me.setUser(response.body().getId(), response.body().getName(), response.body().getEmail(), response.body().getPhone() == null ? -1 : response.body().getPhone(), response.body().getAddress(),
                            response.body().getBirthDate(), response.body().getPhotoUrl());
                    new Handler().postDelayed(() -> {
                        Toast.makeText(MainActivity.this, getString(R.string.restore_success), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }, 1000);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.restore_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetMe Error: ", response.message());
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.restore_error), Toast.LENGTH_SHORT).show();
                Log.i("GetMe Error: ", t.getMessage());
                redirectToLogin();
            }
        });
    }

    // Clean user data and redirect to login page
    private void redirectToLogin() {
        token.clearToken();
        me.clearUser();
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 1500);
    }
}