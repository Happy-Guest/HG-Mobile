package ipl.estg.happyguest.utils.api;

import ipl.estg.happyguest.utils.api.requests.ChangePasswordRequest;
import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.requests.UpdateUserRequest;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @POST("change-password")
    @Headers("Accept: application/json")
    Call<MessageResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    // User
    @GET("me")
    Call<UserResponse> me();

    @POST("users/{id}")
    @Headers("Accept: application/json")
    Call<UserResponse> updateUser(@Body UpdateUserRequest updateUserRequest, @Path("id") int id);

    @DELETE("users/{id}")
    Call<MessageResponse> deleteUser(@Path("id") int id, @Query("password") String password);

    // Codes
    @GET("valid-code")
    Call<HasCodesResponse> hasCodes();

    @GET("users/{id}/codes")
    Call<CodesResponse> getUserCodes(@Path("id") int id, @Query("page") int page);

    @POST("users/{user_id}/codes/{code}/associate")
    Call<MessageResponse> associateCode(@Path("user_id") int user_id, @Path("code") String code);

    @DELETE("users/{user_id}/codes/{code}/disassociate")
    Call<MessageResponse> disassociateCode(@Path("user_id") int user_id, @Path("code") String code);
}
