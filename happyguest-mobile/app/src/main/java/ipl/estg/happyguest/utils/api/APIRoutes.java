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

    // Auth
    @POST("register")
    @Headers({"Accept: application/json", "Content-Type: multipart/form-data;"})
    Call<MessageResponse> register(@Body RegisterRequest registerRequest);
    @POST("login")
    @Headers("Accept: application/json")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("logout")
    Call<MessageResponse> logout();
}
