package ipl.estg.happyguest.app.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private CheckBox remember;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private EditText txtEmail;
    private EditText txtPassword;
    private APIRoutes api;
    private Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Buttons
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);

        // Remember checkbox
        remember = findViewById(R.id.rememberCkeck);

        // TextInputLayouts and EditTexts
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        txtEmail = findViewById(R.id.textEmail);
        txtPassword = findViewById(R.id.textPassword);

        // API Routes and Token
        api = APIClient.getClient(null).create(APIRoutes.class);
        token = new Token(LoginActivity.this);

        // Go to RegisterActivity
        btnGoToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Attempt Login and go to HomeActivity
        btnLogin.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                loginClick();
            } else {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginClick() {
        // Clear errors
        inputEmail.setError(null);
        inputPassword.setError(null);

        // Get values and validate
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if (email.isEmpty()) {
            inputEmail.setError(getString(R.string.email_required));
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getString(R.string.invalid_email));
        } else if (password.isEmpty()) {
            inputPassword.setError(getString(R.string.password_required));
        } else {
            loginAttempt();
        }
    }

    private void loginAttempt() {
        Call<LoginResponse> call = api.login(new LoginRequest(txtEmail.getText().toString(), txtPassword.getText().toString(), remember.isChecked()));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save token
                    token.setToken(response.body().getAccessToken());
                    token.setRemember(remember.isChecked());

                    // Display success message and go to HomeActivity
                    Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    finish();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                if (errors.has("email")) {
                                    inputEmail.setError(errors.getJSONArray("email").get(0).toString());
                                }
                                if (errors.has("password")) {
                                    inputPassword.setError(errors.getJSONArray("password").get(0).toString());
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(LoginActivity.this, getString(R.string.api_error), Toast.LENGTH_LONG).show();
                        Log.i("Login Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, getString(R.string.api_error), Toast.LENGTH_LONG).show();
                Log.i("Login Error: ", t.getMessage());
                call.cancel();
            }
        });
    }

    // Check if device has internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null;
    }
}