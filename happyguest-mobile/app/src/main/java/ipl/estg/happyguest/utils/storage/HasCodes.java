package ipl.estg.happyguest.utils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HasCodes {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public HasCodes(Context context) {
        sharedPreferences = context.getSharedPreferences("hg-code", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setHasCode(Boolean hasCode, String date) {
        editor.putBoolean("hasCode", hasCode);
        editor.putString("date", date);
        editor.commit();
    }

    public Boolean getHasCode() {
        return sharedPreferences.getBoolean("hasCode", false);
    }

    public String getDate() {
        return sharedPreferences.getString("date", null);
    }

    public void clearCode() {
        editor.clear();
        editor.commit();
    }

    public boolean hasCodesAttempt(APIRoutes api) {
        final boolean[] hasCodes = {false};
        Call<HasCodesResponse> call = api.hasCodes();
        call.enqueue(new Callback<HasCodesResponse>() {
            @Override
            public void onResponse(@NonNull Call<HasCodesResponse> call, @NonNull Response<HasCodesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Hide addCode if user has codes
                    setHasCode(response.body().hasCodes(), new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    hasCodes[0] = response.body().hasCodes();
                } else {
                    Log.i("HasCodes Error: ", response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<HasCodesResponse> call, @NonNull Throwable t) {
                Log.i("HasCodes Error: ", t.getMessage());
            }
        });
        return hasCodes[0];
    }
}
