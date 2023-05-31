package ipl.estg.happyguest.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.annotation.Nullable;

import java.io.IOException;

import ipl.estg.happyguest.utils.api.APIClient;
import ipl.estg.happyguest.utils.api.APIRoutes;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;

public class CloseService extends Service {

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
        Token token = new Token(this);
        APIRoutes api = APIClient.getClient().create(APIRoutes.class);
        if (!token.getRemember()) {
            token.clearToken();
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Call<MessageResponse> call = api.logout();
                call.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
