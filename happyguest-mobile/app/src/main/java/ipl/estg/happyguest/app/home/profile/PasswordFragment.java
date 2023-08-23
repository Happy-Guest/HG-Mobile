package ipl.estg.happyguest.app.home.profile;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentPasswordBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.ChangePasswordRequest;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.storage.Token;
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
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPasswordBinding.inflate(inflater, container, false);

        // TextInputLayouts and EditTexts
        inputCurrentPassword = binding.inputCurrentPassword;
        inputNewPassword = binding.inputNewPassword;
        inputConfirmPassword = binding.inputPasswordConfirm;
        txtCurrentPassword = binding.txtCurrentPassword;
        txtNewPassword = binding.txtNewPassword;
        txtConfirmPassword = binding.txtPasswordConfirm;

        // API and Token
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Change password button
        binding.btnChange.setOnClickListener(v -> changePasswordClick());

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.changeFragment(R.id.action2_nav_profile);
            }
        });

        return binding.getRoot();
    }

    public void changePasswordClick() {
        // Clear errors
        inputCurrentPassword.setError(null);
        inputNewPassword.setError(null);
        inputConfirmPassword.setError(null);

        // Get values and validate
        String currentPassword = txtCurrentPassword.getText().toString();
        String newPassword = txtNewPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        if (currentPassword.isEmpty()) {
            inputCurrentPassword.setError(getString(R.string.password_required));
        } else if (newPassword.isEmpty()) {
            inputNewPassword.setError(getString(R.string.new_password_required));
        } else if (newPassword.length() < 5) {
            inputNewPassword.setError(getString(R.string.password_too_short));
        } else if (newPassword.equals(currentPassword)) {
            inputNewPassword.setError(getString(R.string.password_same_as_current));
        } else if (confirmPassword.isEmpty()) {
            inputConfirmPassword.setError(getString(R.string.password_confirmation_required));
        } else if (!confirmPassword.equals(newPassword)) {
            inputConfirmPassword.setError(getString(R.string.password_confirmation_not_match));
        } else {
            showPopup();
        }
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
        ((TextView) popupView.findViewById(R.id.textViewPopUp)).setText(getString(R.string.title_ChangePassword));

        // Show the popup window
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);

        // Close popup
        ImageButton btnPopClose = popupView.findViewById(R.id.btnClose);
        btnPopClose.setOnClickListener(view1 -> popupWindow.dismiss());

        // Confirm popup
        Button btnPopConfirm = popupView.findViewById(R.id.btnConfirm);
        btnPopConfirm.setOnClickListener(view1 -> {
            changePasswordAttempt();
            binding.btnChange.setEnabled(false);
            binding.btnCancel.setEnabled(false);
            popupWindow.dismiss();

        });
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
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
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
                        Log.i("UpdateUser Error: ", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                // Check if this fragment is still attached to the activity
                if (!isAdded()) return;
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("UpdateUser Error: ", Objects.requireNonNull(t.getMessage()));
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