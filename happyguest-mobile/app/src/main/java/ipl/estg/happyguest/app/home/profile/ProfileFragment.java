package ipl.estg.happyguest.app.home.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentProfileBinding;
import ipl.estg.happyguest.utils.User;

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

        // User
        user = new User(binding.getRoot().getContext());

        populateFields();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void populateFields() {
        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(Math.toIntExact(user.getPhone() == null ? 0 : user.getPhone()));
        txtAddress.setText(user.getAddress());
        txtBirthDate.setText(user.getBirthDate());
    }
}