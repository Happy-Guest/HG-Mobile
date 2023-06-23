package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;
    @SerializedName("password")
    private final String password;
    @SerializedName("password_confirmation")
    private final String passwordConfirm;
    @Nullable
    @SerializedName("phone")
    private final Long phone;
    @Nullable
    @SerializedName("photoBase64")
    private final String photoBase64;

    public RegisterRequest(String name, String email, @Nullable Long phone, String password, String passwordConfirm, @Nullable String photoBase64) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.photoBase64 = photoBase64;
    }
}
