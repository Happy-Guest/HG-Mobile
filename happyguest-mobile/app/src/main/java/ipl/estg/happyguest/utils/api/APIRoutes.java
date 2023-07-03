package ipl.estg.happyguest.utils.api;

import ipl.estg.happyguest.utils.api.requests.ChangePasswordRequest;
import ipl.estg.happyguest.utils.api.requests.ComplaintRequest;
import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.requests.ReviewRequest;
import ipl.estg.happyguest.utils.api.requests.UpdateUserRequest;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.ComplaintResponse;
import ipl.estg.happyguest.utils.api.responses.ComplaintsResponse;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.ReviewResponse;
import ipl.estg.happyguest.utils.api.responses.ReviewsResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIRoutes {

    // Auth
    @POST("register")
    @Headers({"Accept: application/json"})
    Call<MessageResponse> register(@Body RegisterRequest registerRequest, @Header("Accept-Language") String language);

    @POST("login")
    @Headers("Accept: application/json")
    Call<LoginResponse> login(@Body LoginRequest loginRequest, @Header("Accept-Language") String language);

    @POST("logout")
    Call<MessageResponse> logout();

    @POST("change-password")
    @Headers("Accept: application/json")
    Call<MessageResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    // User
    @GET("me")
    Call<UserResponse> me(@Header("Accept-Language") String language);

    @POST("users/{id}")
    @Headers("Accept: application/json")
    Call<UserResponse> updateUser(@Body UpdateUserRequest updateUserRequest, @Path("id") int id);

    @DELETE("users/{id}")
    Call<MessageResponse> deleteUser(@Path("id") int id, @Query("password") String password);

    // Codes
    @GET("valid-code")
    Call<HasCodesResponse> hasCodes();

    @GET("users/{id}/codes?order=DESC")
    Call<CodesResponse> getUserCodes(@Path("id") int id, @Query("page") int page, @Query("filter") String filter);

    @POST("users/{user_id}/codes/{code}/associate")
    Call<MessageResponse> associateCode(@Path("user_id") int user_id, @Path("code") String code);

    @DELETE("users/{user_id}/codes/{code}/disassociate")
    Call<MessageResponse> disassociateCode(@Path("user_id") int user_id, @Path("code") String code);

    //Reviews
    @GET("users/{id}/reviews")
    Call<ReviewsResponse> getUserReviews(@Path("id") int id, @Query("page") int page, @Query("order") String order);

    @GET("reviews/{id}")
    Call<ReviewResponse> getReview(@Path("id") Long id);

    @POST("reviews")
    @Headers("Accept: application/json")
    Call<MessageResponse> registerReview(@Body ReviewRequest reviewRequest);

    //Complaints
    @GET("users/{id}/complaints?order=DESC")
    Call<ComplaintsResponse> getUserComplaints(@Path("id") int id, @Query("page") int page, @Query("filter") String filter);

    @GET("complaints/{id}")
    Call<ComplaintResponse> getComplaint(@Path("id") Long id);

    @GET("complaints/{id}/file/{file}")
    Call<ResponseBody> getComplaintFile(@Path("id") Long id, @Path("file") Long file);

    @POST("complaints")
    @Headers("Accept: application/json")
    Call<MessageResponse> registerComplaint(@Body ComplaintRequest complaintRequest);
}
