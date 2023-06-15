package ipl.estg.happyguest.app.home.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.home.HomeActivity;
import ipl.estg.happyguest.databinding.FragmentProfileBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.UpdateUserRequest;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import ipl.estg.happyguest.utils.others.Token;
import ipl.estg.happyguest.utils.others.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextInputLayout inputName;
    private TextInputLayout inputEmail;
    private TextInputLayout inputPhone;
    private TextInputLayout inputAddress;
    private TextInputLayout inputBirthDate;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPhone;
    private EditText txtAddress;
    private EditText txtBirthDate;
    private Button btnSave;
    private Button btnCancel;
    private User user;
    private APIRoutes api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // TextInputLayouts and EditTexts
        inputName = binding.inputName;
        inputEmail = binding.inputEmail;
        inputPhone = binding.inputPhoneNr;
        inputAddress = binding.inputAddress;
        inputBirthDate = binding.inputBirthDate;
        txtName = binding.textName;
        txtEmail = binding.textEmail;
        txtPhone = binding.textPhoneNr;
        txtAddress = binding.textAddress;
        txtBirthDate = binding.textBirthDate;

        // User, API and Token
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        populateFields();

        // Edit button
        binding.btnEdit.setOnClickListener(v -> changeFieldsState(true));

        // Cancel button
        btnCancel = binding.btnCancel;
        btnCancel.setOnClickListener(v -> {
            changeFieldsState(false);
            populateFields();
        });

        // Save button
        btnSave = binding.btnSave;
        btnSave.setOnClickListener(v -> {
            if (btnSave.isEnabled()) validateFields();
        });

        // Add "/" to birth date
        final int[] birthDateLength = {txtBirthDate.getText().toString().length()};
        txtBirthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                birthDateLength[0] = txtBirthDate.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String birthDate = txtBirthDate.getText().toString();
                if ((birthDate.length() == 2 || birthDate.length() == 5) && !birthDate.endsWith("/") && birthDateLength[0] < birthDate.length()) {
                    txtBirthDate.append("/");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String birthDate = txtBirthDate.getText().toString();
                if (birthDate.length() == 2 && !birthDate.endsWith("/")) {
                    txtBirthDate.setText(birthDate.substring(0, 1));
                    txtBirthDate.setSelection(txtBirthDate.getText().length());
                } else if (birthDate.length() == 5 && !birthDate.endsWith("/")) {
                    txtBirthDate.setText(birthDate.substring(0, 4));
                    txtBirthDate.setSelection(txtBirthDate.getText().length());
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void changeFieldsState(boolean state) {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.setEditMode(state);
        }
        txtName.setEnabled(state);
        txtEmail.setEnabled(state);
        txtPhone.setEnabled(state);
        txtAddress.setEnabled(state);
        txtBirthDate.setEnabled(state);
        if (state) {
            binding.btnSave.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in));
            binding.btnCancel.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in));
            binding.btnEdit.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out));
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.btnEdit.setVisibility(View.GONE);
        } else {
            binding.btnSave.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out));
            binding.btnCancel.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_out));
            binding.btnEdit.setAnimation(AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.fade_in));
            binding.btnSave.setVisibility(View.GONE);
            binding.btnCancel.setVisibility(View.GONE);
            binding.btnEdit.setVisibility(View.VISIBLE);
            inputName.setError(null);
            inputEmail.setError(null);
            inputPhone.setError(null);
            inputAddress.setError(null);
            inputBirthDate.setError(null);
        }
    }

    private void populateFields() {
        try {
            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhone() == -1 ? "" : user.getPhone().toString());
            txtAddress.setText(user.getAddress() == null ? "" : user.getAddress());
            if (user.getBirthDate() != null) {
                txtBirthDate.setText(String.format(getString(R.string.slash_placeholder), user.getBirthDate()));
            } else {
                txtBirthDate.setText("");
            }
            if (user.getPhotoUrl() != null) {
                if (getActivity() instanceof HomeActivity) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.populateImageProfile();
                }
            }
        } catch (Exception e) {
            Toast.makeText(binding.getRoot().getContext(), getString(R.string.data_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void validateFields() {
        // Clear errors
        inputName.setError(null);
        inputEmail.setError(null);
        inputPhone.setError(null);
        inputAddress.setError(null);
        inputBirthDate.setError(null);

        // Get values and validate
        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtPhone.getText().toString();
        String birthDate = txtBirthDate.getText().toString();
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
        } else if (!birthDate.isEmpty() && birthDate.length() != 10) {
            inputBirthDate.setError(getString(R.string.invalid_birth_date));
        } else {
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
            updateAttempt();
        }
    }

    private String formatDate(String date) {
        String[] dateArray = date.split("/");
        return dateArray[2] + "/" + dateArray[1] + "/" + dateArray[0];
    }

    private void updateAttempt() {
        // Get photo
        byte[] photo = null;
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            photo = homeActivity.getPhoto();
        }
        Call<UserResponse> call = api.updateUser(new UpdateUserRequest(
                txtName.getText().toString(),
                txtEmail.getText().toString(),
                txtPhone.getText().toString().isEmpty() ? null : Long.parseLong(txtPhone.getText().toString()),
                txtAddress.getText().toString().isEmpty() ? null : txtAddress.getText().toString(),
                txtBirthDate.getText().toString().isEmpty() ? null : formatDate(txtBirthDate.getText().toString()),
                photo == null ? null : Base64.encodeToString(photo, Base64.DEFAULT)), user.getId());
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Display success message and update user
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    user.setUser(response.body().getUser().getId(), response.body().getUser().getName(), response.body().getUser().getEmail(), response.body().getUser().getPhone() == null ? -1 : response.body().getUser().getPhone(), response.body().getUser().getAddress(),
                            response.body().getUser().getBirthDate(), response.body().getUser().getPhotoUrl());
                    changeFieldsState(false);
                    populateFields();
                } else {
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
                                if (errors.has("phone")) {
                                    inputPhone.setError(errors.getJSONArray("phone").get(0).toString());
                                }
                                if (errors.has("address")) {
                                    inputAddress.setError(errors.getJSONArray("address").get(0).toString());
                                }
                                if (errors.has("birth_date")) {
                                    inputBirthDate.setError(errors.getJSONArray("birth_date").get(0).toString());
                                }
                                if (errors.has("photo")) {
                                    Toast.makeText(binding.getRoot().getContext(), errors.getJSONArray("photo").get(0).toString(), Toast.LENGTH_SHORT).show();
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
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.i("UpdateUser Error: ", t.getMessage());
                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
            }
        });
    }
}