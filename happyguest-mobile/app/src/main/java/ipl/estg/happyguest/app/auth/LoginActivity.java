package ipl.estg.happyguest.app.auth;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    CheckBox remember;
    TextInputLayout inputEmail;
    TextInputLayout inputPassword;
    EditText txtEmail;
    EditText txtPassword;
    APIRoutes api;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Buttons
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);

        // Remember checkbox
        remember = findViewById(R.id.rememberCkeck);

        // TextInputLayouts
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        // EditTexts
        txtEmail = findViewById(R.id.textEmail);
        txtPassword = findViewById(R.id.textPassword);

        // API Routes
        api = APIClient.getClient().create(APIRoutes.class);

        // Go to RegisterActivity
        btnGoToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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

        // Get values
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        // Validate values
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
        Call<ResponseBody> call = api.login(new LoginRequest(txtEmail.getText().toString(), txtPassword.getText().toString(), remember.isChecked()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                // Check if response is successful
                if (response.isSuccessful()) {
                    // Get response body
                    LoginResponse loginResponse = new LoginResponse(Objects.requireNonNull(response.body()));
                    // Save token
                    token = new Token(LoginActivity.this);
                    token.setToken(loginResponse.accessToken);
                    token.setRemember(remember.isChecked());
                    // Display success message
                    Toast.makeText(LoginActivity.this, loginResponse.message, Toast.LENGTH_LONG).show();
                    // Go to HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                } else {
                    try {
                        // Get response errors
                        JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
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
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                call.cancel();
                Toast.makeText(LoginActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
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