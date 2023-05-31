package ipl.estg.happyguest.utils.api;

import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIRoutes {
    @Headers("Accept: application/json")

    // Auth
    @POST("register")
    Call<MessageResponse> register(@Body RegisterRequest registerRequest);
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("logout")
    Call<MessageResponse> logout();
}
