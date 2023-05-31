package ipl.estg.happyguest.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ipl.estg.happyguest.R;
import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CloseService extends Service {

    private Token token;
    private APIRoutes api;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        token = new Token(this);
        api = APIClient.getClient().create(APIRoutes.class);
        APIClient.setToken(token.getToken());
        if (!token.getRemember()) {
            Toast.makeText(this, getString(R.string.session_terminated), Toast.LENGTH_SHORT).show();
            logout();
        }
    }

    private void logout() {
        Call<MessageResponse> call = api.logout();
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    token.clearToken();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                Toast.makeText(CloseService.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });
    }
}
