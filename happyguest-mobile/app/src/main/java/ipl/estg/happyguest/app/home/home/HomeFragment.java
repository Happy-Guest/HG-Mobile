package ipl.estg.happyguest.app.home.home;

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
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.databinding.InsertCodeBinding;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.User;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private InsertCodeBinding insertCodeBinding;
    private TextInputLayout inputCode;
    private EditText txtCode;
    private APIRoutes api;
    private Token token;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        insertCodeBinding = InsertCodeBinding.inflate(inflater, container, false);

        // TextInputLayouts and EditTexts
        inputCode = insertCodeBinding.inputCode;
        txtCode = insertCodeBinding.textCode;

        //Get values
        String code = txtCode.getText().toString();

        // API and Token
        token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        //Validate values
        if (code.isEmpty()) {
            inputCode.setError(getString(R.string.code_required));
        }


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}