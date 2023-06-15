package ipl.estg.happyguest.app.home.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.RegisterActivity;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentPasswordBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.ChangePasswordRequest;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import ipl.estg.happyguest.utils.others.Token;
import ipl.estg.happyguest.utils.others.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordFragment extends Fragment {

    private FragmentPasswordBinding binding;
    private TextInputLayout inputCurrentPassword;
    private TextInputLayout inputNewPassword;
    private TextInputLayout inputConfirmPassword;
    private EditText txtCurrentPassword;
    private EditText txtNewPassword;
    private EditText txtConfirmPassword;
    private User user;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasswordBinding.inflate(inflater, container, false);

        //TextInputLayouts and EditTexts
        inputCurrentPassword = binding.inputCurrentPassword;
        inputNewPassword = binding.inputNewPassword;
        inputConfirmPassword = binding.inputPasswordConfirm;
        txtCurrentPassword = binding.txtCurrentPassword;
        txtNewPassword = binding.txtNewPassword;
        txtConfirmPassword = binding.txtPasswordConfirm;

        // User, API and Token
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Change password button
        binding.btnChange.setOnClickListener(v -> {
            changePasswordClick();
        });

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.nav_profile);
            }
        });


        return binding.getRoot();
    }

    public void changePasswordClick() {
        //clear errors
        inputCurrentPassword.setError(null);
        inputNewPassword.setError(null);
        inputConfirmPassword.setError(null);

        //Get values and validate
        String currentPassword = txtCurrentPassword.getText().toString();
        String newPassword = txtNewPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        if (currentPassword.isEmpty()) {
            inputCurrentPassword.setError(getString(R.string.password_required));
        } else if (newPassword.isEmpty()) {
            inputNewPassword.setError(getString(R.string.new_password_required));
        } else if (newPassword.length() < 5) {
            inputNewPassword.setError(getString(R.string.password_too_short));
        } else if (newPassword.equals(currentPassword)){
            inputNewPassword.setError(getString(R.string.password_same_as_current));
        } else if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError(getString(R.string.password_confirmation_required));
        } else if (!confirmPassword.equals(newPassword)) {
            inputConfirmPassword.setError(getString(R.string.password_confirmation_not_match));
        } else {
            binding.btnChange.setEnabled(false);
            binding.btnCancel.setEnabled(false);
            changePasswordAttempt();
        }

    }

    private void changePasswordAttempt() {
        Call<MessageResponse> call = api.changePassword(new ChangePasswordRequest(
                txtCurrentPassword.getText().toString(),
                txtNewPassword.getText().toString(),
                txtConfirmPassword.getText().toString()
        ));

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                binding.btnChange.setEnabled(true);
                binding.btnCancel.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and update user
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof HomeActivity) {
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.changeFragment(R.id.nav_profile);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            // Get response errors
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("errors")) {
                                JSONObject errors = jObjError.getJSONObject("errors");
                                if (errors.has("old_password")) {
                                    inputCurrentPassword.setError(errors.getJSONArray("old_password").get(0).toString());
                                }
                                if (errors.has("new_password")) {
                                    inputNewPassword.setError(errors.getJSONArray("new_password").get(0).toString());
                                }
                                if (errors.has("new_password_confirmation")) {
                                    inputConfirmPassword.setError(errors.getJSONArray("confirm_password").get(0).toString());
                                }
                            } else {
                                Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("UpdateUser Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("UpdateUser Error: ", t.getMessage());
                binding.btnChange.setEnabled(true);
                binding.btnCancel.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}