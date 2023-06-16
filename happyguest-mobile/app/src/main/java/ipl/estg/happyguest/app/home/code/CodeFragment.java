package ipl.estg.happyguest.app.home.code;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.databinding.FragmentCodeBinding;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.models.Code;
import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.UserCode;
import ipl.estg.happyguest.utils.storage.HasCodes;
import ipl.estg.happyguest.utils.storage.Token;
import ipl.estg.happyguest.utils.storage.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeFragment extends Fragment {

    private FragmentCodeBinding binding;
    private Button btnInsertCode;
    private TextInputLayout inputCode;
    private User user;
    private APIRoutes api;
    private CodesAdapter codesAdapter;
    private ArrayList<Code> codesList;
    private Meta meta;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCodeBinding.inflate(inflater, container, false);

        // User, Token, Code and API
        user = new User(binding.getRoot().getContext());
        Token token = new Token(binding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);
        HasCodes hasCodes = new HasCodes(binding.getRoot().getContext());

        // Associate code button
        inputCode = binding.addCode.inputCode;
        btnInsertCode = binding.addCode.btnAssociate;
        btnInsertCode.setOnClickListener(v -> associateCode());

        // Hide textCode
        binding.addCode.txtCodeTitle.setText(R.string.code_associate);
        if (hasCodes.getHasCode()) {
            binding.addCode.txtCodeText.setVisibility(View.GONE);
        }

        // Codes
        RecyclerView codesRV = binding.codesRV;
        codesList = new ArrayList<>();
        codesAdapter = new CodesAdapter(codesList, binding.getRoot().getContext());
        codesRV.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        codesRV.setAdapter(codesAdapter);
        getCodesAttempt(1);

        // Get codes on scroll
        binding.codesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView codesRV, int dx, int dy) {
                super.onScrolled(codesRV, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) codesRV.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == codesList.size() - 1) {
                    if (meta != null && meta.getCurrentPage() < meta.getLastPage()) {
                        getCodesAttempt(meta.getCurrentPage() + 1);
                    }
                }
            }
        });

        return binding.getRoot();
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
                    HasCodes hasCodes = new HasCodes(binding.getRoot().getContext());
                    hasCodes.setHasCode(true, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    binding.addCode.txtCodeText.setVisibility(View.GONE);
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

    private void getCodesAttempt(int page) {
        Call<CodesResponse> call = api.getUserCodes(user.getId(), page);
        call.enqueue(new Callback<CodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<CodesResponse> call, @NonNull Response<CodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save codes in list and update adapter
                    int lastPos = codesList.size();
                    ArrayList<UserCode> userCodes = response.body().getData();
                    for (UserCode userCode : userCodes) {
                        codesList.add(userCode.getCode());
                    }
                    meta = response.body().getMeta();
                    codesAdapter.notifyItemRangeInserted(lastPos, userCodes.size());
                } else {
                    Toast.makeText(binding.getRoot().getContext(), getString(R.string.codes_error), Toast.LENGTH_SHORT).show();
                    Log.i("GetCodes Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CodesResponse> call, @NonNull Throwable t) {
                Toast.makeText(binding.getRoot().getContext(), getString(R.string.codes_error), Toast.LENGTH_SHORT).show();
                Log.i("GetCodes Error: ", t.getMessage());
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}