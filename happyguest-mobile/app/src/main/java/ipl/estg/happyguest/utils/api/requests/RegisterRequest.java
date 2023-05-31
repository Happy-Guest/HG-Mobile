package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;

public class RegisterRequest {

    @SerializedName("name")
    public String name;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("password_confirmation")
    public String passwordConfirm;
    @SerializedName("phone")
    public Long phone;
    @SerializedName("photo")
    public MultipartBody.Part photo;

    public RegisterRequest(String name, String email, Long phone, String password, String passwordConfirm, MultipartBody.Part photo) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.photo = photo;
    }
}
