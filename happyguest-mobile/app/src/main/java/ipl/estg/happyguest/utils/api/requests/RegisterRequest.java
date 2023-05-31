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
    public String passwordConfirmation;
    @SerializedName("phone")
    public Long phone;

    public RegisterRequest(String name, String email, String password, String passwordConfirmation, Long phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.phone = phone;
    }
}
