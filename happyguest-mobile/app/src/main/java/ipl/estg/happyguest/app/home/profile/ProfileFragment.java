package ipl.estg.happyguest.app.home.profile;

import android.os.Bundle;
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
        api = APIClient.getClient().create(APIRoutes.class);

        populateFields();

        // Edit button
        binding.btnEdit.setOnClickListener(v -> {
            txtName.setEnabled(true);
            txtEmail.setEnabled(true);
            txtPhone.setEnabled(true);
            txtAddress.setEnabled(true);
            txtBirthDate.setEnabled(true);
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.btnEdit.setVisibility(View.INVISIBLE);
        });

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> {
            txtName.setEnabled(false);
            txtEmail.setEnabled(false);
            txtPhone.setEnabled(false);
            txtAddress.setEnabled(false);
            txtBirthDate.setEnabled(false);
            binding.btnSave.setVisibility(View.INVISIBLE);
            binding.btnCancel.setVisibility(View.INVISIBLE);
            binding.btnEdit.setVisibility(View.VISIBLE);
            populateFields();
        });

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
}