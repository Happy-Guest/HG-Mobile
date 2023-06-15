package ipl.estg.happyguest.app.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private APIRoutes api;
    private TextInputLayout inputName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPassword;
    private TextInputLayout inputPasswordConfirm;
    private TextInputLayout inputPhone;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtPasswordConfirm;
    private Button btnRegister;
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
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            photo = stream.toByteArray();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Buttons
        btnRegister = findViewById(R.id.btnRegister);
        FloatingActionButton btnImage = findViewById(R.id.btnImage);

        // TextInputLayouts and EditTexts
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordConfirm = findViewById(R.id.inputPasswordConfirm);
        inputPhone = findViewById(R.id.inputPhoneNr);
        txtName = findViewById(R.id.textName);
        txtEmail = findViewById(R.id.textEmail);
        txtPhone = findViewById(R.id.textPhoneNr);
        txtPassword = findViewById(R.id.textPassword);
        txtPasswordConfirm = findViewById(R.id.textPasswordConfirm);

        // API Routes
        api = APIClient.getClient(null).create(APIRoutes.class);

        // Select Image
        btnImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent();
            photoPicker.setType("image/*");
            photoPicker.setAction(Intent.ACTION_GET_CONTENT);
            startActivityResult.launch(Intent.createChooser(photoPicker, getString(R.string.select_image)));
        });

        // Attempt Register and go to LoginActivity
        btnRegister.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                if (btnRegister.isEnabled()) {
                    registerClick();
                }
            } else {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerClick() {
        //Clear errors
        inputName.setError(null);
        inputEmail.setError(null);
        inputPhone.setError(null);
        inputPassword.setError(null);
        inputPasswordConfirm.setError(null);

        //Get values and validate
        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        String passwordConfirmation = txtPasswordConfirm.getText().toString();
        if (name.isEmpty()) {
            inputName.setError(getString(R.string.name_required));
        } else if (name.length() < 3) {
            inputName.setError(getString(R.string.name_too_short));
        } else if (email.isEmpty()) {
            inputEmail.setError(getString(R.string.email_required));
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError(getString(R.string.invalid_email));
        } else if (!phone.isEmpty() && phone.length() < 9 || phone.length() > 12) {
            inputPhone.setError(getString(R.string.invalid_phone));
        } else if (password.isEmpty()) {
            inputPassword.setError(getString(R.string.password_required));
        } else if (password.length() < 5) {
            inputPassword.setError(getString(R.string.password_too_short));
        } else if (passwordConfirmation.isEmpty()) {
            inputPasswordConfirm.setError(getString(R.string.password_confirmation_required));
        } else if (!passwordConfirmation.equals(password)) {
            inputPasswordConfirm.setError(getString(R.string.password_confirmation_not_match));
        } else {
            btnRegister.setEnabled(false);
            registerAttempt();
        }
    }

    private void registerAttempt() {
        Call<MessageResponse> call = api.register(new RegisterRequest(
                txtName.getText().toString(),
                txtEmail.getText().toString(),
                txtPhone.getText().toString().isEmpty() ? null : Long.parseLong(txtPhone.getText().toString()),
                txtPassword.getText().toString(),
                txtPasswordConfirm.getText().toString(), photo == null ? null : Base64.encodeToString(photo, Base64.DEFAULT)));
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and go to LoginActivity
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    btnRegister.setEnabled(true);
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                if (errors.has("name")) {
                                    inputName.setError(errors.getJSONArray("name").get(0).toString());
                                }
                                if (errors.has("email")) {
                                    inputEmail.setError(errors.getJSONArray("email").get(0).toString());
                                }
                                if (errors.has("password")) {
                                    inputPassword.setError(errors.getJSONArray("password").get(0).toString());
                                }
                                if (errors.has("password_confirmation")) {
                                    inputPasswordConfirm.setError(errors.getJSONArray("password_confirmation").get(0).toString());
                                }
                                if (errors.has("phone")) {
                                    inputPhone.setError(errors.getJSONArray("phone").get(0).toString());
                                }
                                if (errors.has("photo")) {
                                    Toast.makeText(RegisterActivity.this, errors.getJSONArray("photo").get(0).toString(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("Register Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(RegisterActivity.this, getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.e("Register Error: ", t.getMessage());
                btnRegister.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    // Check if device has internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null;
    }
}