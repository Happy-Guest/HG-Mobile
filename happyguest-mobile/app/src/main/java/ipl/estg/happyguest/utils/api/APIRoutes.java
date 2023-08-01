package ipl.estg.happyguest.utils.api;

import ipl.estg.happyguest.utils.api.requests.ChangePasswordRequest;
import ipl.estg.happyguest.utils.api.requests.CheckOutRequest;
import ipl.estg.happyguest.utils.api.requests.ComplaintRequest;
import ipl.estg.happyguest.utils.api.requests.LoginRequest;
import ipl.estg.happyguest.utils.api.requests.OrderRequest;
import ipl.estg.happyguest.utils.api.requests.RegisterRequest;
import ipl.estg.happyguest.utils.api.requests.ReserveRequest;
import ipl.estg.happyguest.utils.api.requests.ReviewRequest;
import ipl.estg.happyguest.utils.api.requests.UpdateStatusRequest;
import ipl.estg.happyguest.utils.api.requests.UpdateUserRequest;
import ipl.estg.happyguest.utils.api.responses.CodesResponse;
import ipl.estg.happyguest.utils.api.responses.ComplaintResponse;
import ipl.estg.happyguest.utils.api.responses.ComplaintsResponse;
import ipl.estg.happyguest.utils.api.responses.HasCodesResponse;
import ipl.estg.happyguest.utils.api.responses.HotelResponse;
import ipl.estg.happyguest.utils.api.responses.LoginResponse;
import ipl.estg.happyguest.utils.api.responses.MessageResponse;
import ipl.estg.happyguest.utils.api.responses.OrderResponse;
import ipl.estg.happyguest.utils.api.responses.OrdersResponse;
import ipl.estg.happyguest.utils.api.responses.RegionResponse;
import ipl.estg.happyguest.utils.api.responses.ReserveResponse;
import ipl.estg.happyguest.utils.api.responses.ReservesResponse;
import ipl.estg.happyguest.utils.api.responses.ReviewResponse;
import ipl.estg.happyguest.utils.api.responses.ReviewsResponse;
import ipl.estg.happyguest.utils.api.responses.ServiceResponse;
import ipl.estg.happyguest.utils.api.responses.UserResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIRoutes {

    // Auth

    @POST("register")
    @Headers({"Accept: application/json"})
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
    Call<UserResponse> me(@Header("Accept-Language") String language);

    @POST("users/{id}")
    @Headers("Accept: application/json")
    Call<UserResponse> updateUser(@Body UpdateUserRequest updateUserRequest, @Path("id") Long id);

    @DELETE("users/{id}")
    Call<MessageResponse> deleteUser(@Path("id") Long id, @Query("password") String password);

    // Codes

    @GET("valid-code")
    Call<HasCodesResponse> hasCodes();

    @GET("users/{id}/codes?order=DESC")
    Call<CodesResponse> getUserCodes(@Path("id") Long id, @Query("page") int page, @Query("filter") String filter);

    @POST("users/{user_id}/codes/{code}/associate")
    Call<MessageResponse> associateCode(@Path("user_id") Long user_id, @Path("code") String code);

    @DELETE("users/{user_id}/codes/{code}/disassociate")
    Call<MessageResponse> disassociateCode(@Path("user_id") Long user_id, @Path("code") String code);

    // Reviews

    @GET("users/{id}/reviews")
    Call<ReviewsResponse> getUserReviews(@Path("id") Long id, @Query("page") int page, @Query("order") String order);

    @GET("reviews/{id}")
    Call<ReviewResponse> getReview(@Path("id") Long id);

    @POST("reviews")
    @Headers("Accept: application/json")
    Call<MessageResponse> registerReview(@Body ReviewRequest reviewRequest);

    // Complaints

    @GET("users/{id}/complaints?order=DESC")
    Call<ComplaintsResponse> getUserComplaints(@Path("id") Long id, @Query("page") int page, @Query("filter") String filter);

    @GET("complaints/{id}")
    Call<ComplaintResponse> getComplaint(@Path("id") Long id);

    @GET("complaints/{id}/file/{file}")
    Call<ResponseBody> getComplaintFile(@Path("id") Long id, @Path("file") Long file);

    @POST("complaints")
    @Headers("Accept: application/json")
    Call<MessageResponse> registerComplaint(@Body ComplaintRequest complaintRequest);

    // Services

    @GET("services/{id}")
    Call<ServiceResponse> getService(@Path("id") Long id);

    // Orders

    @GET("users/{id}/orders?order=DESC")
    Call<OrdersResponse> getUserOrders(@Path("id") Long id, @Query("page") int page, @Query("filter") String filter);

    @GET("orders/{id}")
    Call<OrderResponse> getOrder(@Path("id") Long id);

    @PATCH("orders/{id}")
    @Headers("Accept: application/json")
    Call<MessageResponse> cancelOrder(@Body UpdateStatusRequest updateStatusRequest, @Path("id") Long id);

    @POST("orders")
    @Headers("Accept: application/json")
    Call<MessageResponse> registerOrder(@Body OrderRequest orderRequest);

    // Reserves

    @GET("users/{id}/reserves?order=DESC")
    Call<ReservesResponse> getUserReserves(@Path("id") Long id, @Query("page") int page, @Query("filter") String filter);

    @GET("reserves/{id}")
    Call<ReserveResponse> getReserve(@Path("id") Long id);

    @PATCH("reserves/{id}")
    @Headers("Accept: application/json")
    Call<MessageResponse> cancelReserve(@Body UpdateStatusRequest updateStatusRequest, @Path("id") Long id);

    @POST("reserves")
    @Headers("Accept: application/json")
    Call<MessageResponse> registerReserve(@Body ReserveRequest reserveRequest);

    // Hotel

    @GET("hotels/{id}")
    Call<HotelResponse> getHotel(@Path("id") Long id);

    // Region
    @GET("regions/{id}")
    Call<RegionResponse> getRegion(@Path("id") Long id);

    //CheckOut
    @POST("checkouts")
    @Headers("Accept: application/json")
    Call<MessageResponse> checkOut(@Body CheckOutRequest checkOutRequest);
}
