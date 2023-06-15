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
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.others.Code;
import ipl.estg.happyguest.utils.others.Token;
import ipl.estg.happyguest.utils.others.User;
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

        // See if user has codes
        hasCodesAttempt();

        // Associate code button
        inputCode = binding.addCode.inputCode;
        btnInsertCode = binding.addCode.btnAssociate;
        btnInsertCode.setOnClickListener(v -> associateCode());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (code.getHasCode()) {
            binding.codeLayout.setVisibility(View.GONE);
        } else {
            binding.codeLayout.setVisibility(View.VISIBLE);
        }
    }

    private void associateCode() {
        // Reset errors
        inputCode.setError(null);
        String code = Objects.requireNonNull(binding.addCode.textCode.getText()).toString();

        // Validate code
        if (code.isEmpty()) {
            inputCode.setError(getString(R.string.code_required));
        } else {
            if (btnInsertCode.isEnabled()) {
                btnInsertCode.setEnabled(false);
                associateCodeAttempt(code);
            }
        }
    }

    private void associateCodeAttempt(String code) {
        Call<MessageResponse> call = api.associateCode(user.getId(), code);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                btnInsertCode.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Code code = new Code(binding.getRoot().getContext());
                    code.setHasCode(true);
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

    private void hasCodesAttempt() {
        Call<HasCodesResponse> call = api.hasCodes(user.getId());
        call.enqueue(new Callback<HasCodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HasCodesResponse> call, @NonNull Response<HasCodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Hide addCode if user has codes
                    if (response.body().hasCodes()) {
                        binding.codeLayout.setVisibility(View.GONE);
                        code.setHasCode(true);
                    } else {
                        binding.codeLayout.setVisibility(View.VISIBLE);
                        code.setHasCode(false);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}