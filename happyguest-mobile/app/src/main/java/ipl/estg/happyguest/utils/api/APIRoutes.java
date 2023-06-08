package ipl.estg.happyguest.utils.api;

import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.requests.UpdateUserRequest;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIRoutes {

    // Auth
    @POST("register")
    @Headers("Accept: application/json")
    Call<MessageResponse> register(@Body RegisterRequest registerRequest);

    @POST("login")
    @Headers("Accept: application/json")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("logout")
    Call<MessageResponse> logout();

    // User
    @GET("me")
    Call<UserResponse> me();

    @POST("users/{id}")
    Call<UserResponse> updateUser(@Body UpdateUserRequest updateUserRequest, @Path("id") int id);
}
