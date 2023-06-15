package ipl.estg.happyguest.app.home.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.storage.Code;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Button btnInsertCode;
    private TextInputLayout inputCode;
    private User user;
    private APIRoutes api;
    private Code code;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // User, Token, Code and API
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);
        code = new Code(binding.getRoot().getContext());

        // Check if user has codes
        if (code.getHasCode() && Objects.equals(code.getDate(), new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()))) {
            binding.codeLayout.setVisibility(View.GONE);
        } else if (code.getHasCode() && !Objects.equals(code.getDate(), new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()))) {
            hasCodesAttempt();
        } else {
            binding.codeLayout.setVisibility(View.VISIBLE);
        }

        // Associate code button
        inputCode = binding.addCode.inputCode;
        btnInsertCode = binding.addCode.btnAssociate;
        btnInsertCode.setOnClickListener(v -> associateCode());

        return binding.getRoot();
    }

    private void associateCode() {
        // Reset errors
        inputCode.setError(null);
        String codeTxt = Objects.requireNonNull(binding.addCode.textCode.getText()).toString();

        // Validate code
        if (codeTxt.isEmpty()) {
            inputCode.setError(getString(R.string.code_required));
        } else {
            if (btnInsertCode.isEnabled()) {
                btnInsertCode.setEnabled(false);
                associateCodeAttempt(codeTxt);
            }
        }
    }

    private void hasCodesAttempt() {
        Call<HasCodesResponse> call = api.hasCodes(user.getId());
        call.enqueue(new Callback<HasCodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HasCodesResponse> call, @NonNull Response<HasCodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    code.setHasCode(response.body().hasCodes(), new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    if (code.getHasCode()) {
                        binding.codeLayout.setVisibility(View.GONE);
                    } else {
                        binding.codeLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.i("HasCodes Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HasCodesResponse> call, @NonNull Throwable t) {
                Log.i("HasCodes Error: ", t.getMessage());
            }
        });
    }

    private void associateCodeAttempt(String codeTxt) {
        Call<MessageResponse> call = api.associateCode(user.getId(), codeTxt);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                btnInsertCode.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // Set hasCode to true and hide code layout
                    Code code = new Code(binding.getRoot().getContext());
                    code.setHasCode(true, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    binding.codeLayout.setVisibility(View.GONE);
                    Toast.makeText(binding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (response.code() == 404) {
                        inputCode.setError(binding.getRoot().getContext().getString(R.string.invalid_code));
                        return;
                    }
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("message")) {
                                inputCode.setError(jObjError.getString("message"));
                            } else {
                                Toast.makeText(binding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                        Log.i("AssociateCode Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.api_error), Toast.LENGTH_SHORT).show();
                Log.e("AssociateCode Error: ", t.getMessage());
                btnInsertCode.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}