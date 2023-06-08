package ipl.estg.happyguest.app.home.home;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.app.auth.LoginActivity;
import ipl.estg.happyguest.app.auth.RegisterActivity;
import ipl.estg.happyguest.databinding.FragmentHomeBinding;
import ipl.estg.happyguest.databinding.InsertCodeBinding;
import ipl.estg.happyguest.utils.Token;
import ipl.estg.happyguest.utils.User;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.requests.CodeRequest;
import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertCode {

    private TextInputLayout inputCode;
    private EditText txtCode;

    private User user;
    private APIRoutes api;
    private Token token;

    public void insertCode(FragmentHomeBinding fragmentHomeBinding) {

        // TextInputLayouts and EditTexts
        inputCode = fragmentHomeBinding.getRoot().findViewById(R.id.inputCode);
        txtCode = fragmentHomeBinding.getRoot().findViewById(R.id.textCode);

        inputCode.setError(null);
        String code = txtCode.getText().toString();


        user = new User(fragmentHomeBinding.getRoot().getContext());
        token = new Token(fragmentHomeBinding.getRoot().getContext());
        api = APIClient.getClient(token.getToken()).create(APIRoutes.class);

        if (code.isEmpty()) {
            inputCode.setError("O código não pode estar vazio");
        }
        else {
            insertCodeAttempt(fragmentHomeBinding);
        }
    }

    private void insertCodeAttempt( FragmentHomeBinding fragmentHomeBinding) {
        Call<MessageResponse> call = api.codes(user.getId(), txtCode.getText().toString());
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(fragmentHomeBinding.getRoot().getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("message")) {
                                    inputCode.setError(jObjError.getString("message"));
                            } else {
                                Toast.makeText(fragmentHomeBinding.getRoot().getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException | IOException e) {
                        Toast.makeText(fragmentHomeBinding.getRoot().getContext(), "Error connecting to the server!", Toast.LENGTH_LONG).show();
                        Log.i("Code Error: ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(fragmentHomeBinding.getRoot().getContext(), "Error connecting to the server!", Toast.LENGTH_LONG).show();
                Log.e("Code Error: ", t.getMessage());
                call.cancel();
            }
        });
    }
}
