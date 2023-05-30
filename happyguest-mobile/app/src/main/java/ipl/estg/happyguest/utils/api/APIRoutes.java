package ipl.estg.happyguest.utils.api;

import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIRoutes {
    @Headers("Accept: application/json")

    @POST("login")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);
}
