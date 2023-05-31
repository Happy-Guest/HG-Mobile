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

    public RegisterRequest(String name, String email, Long phone, String password, String passwordConfirm) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
