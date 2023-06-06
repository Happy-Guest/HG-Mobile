package ipl.estg.happyguest.app.home.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentProfileBinding;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.User;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;

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
    private User user;
    private APIRoutes api;
    private Token token;

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
        token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        // Get user data if it's not already loaded
        if (user.getName() == null) {
            getMeAttempt();
        } else {
            populateFields();
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateFields() {
        try {
            txtName.setText(user.getName());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhone() == -1 ? "" : user.getPhone().toString());
            txtAddress.setText(user.getAddress() == null ? "" : user.getAddress());
            txtBirthDate.setText(user.getBirthDate() == null ? "" : user.getBirthDate());
        } catch (Exception e) {
            Toast.makeText(binding.getRoot().getContext(), getString(R.string.data_error), Toast.LENGTH_LONG).show();
        }
    }

    private void getMeAttempt() {
        Call<UserResponse> call = api.me();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user.setUser(response.body().getId(), response.body().getName(), response.body().getEmail(), response.body().getPhone(), response.body().getAddress(),
                            response.body().getBirthDate() == null ? "" : response.body().getBirthDate(), response.body().getPhotoUrl());
                    populateFields();
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetMe Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.data_error), Toast.LENGTH_SHORT).show();
                Log.i("GetMe Error: ", t.getMessage());
                call.cancel();
            }
        });
    }
}