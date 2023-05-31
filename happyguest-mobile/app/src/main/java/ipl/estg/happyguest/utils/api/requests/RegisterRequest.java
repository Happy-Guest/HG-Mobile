package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("photoBase64")
    public String photoBase64;

    public RegisterRequest(String name, String email, Long phone, String password, String passwordConfirm, String photoBase64) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.photoBase64 = photoBase64;
    }
}
