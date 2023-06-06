package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    private final String email;
    @SerializedName("password")
    private final String password;
    @SerializedName("remember")
    public final boolean remember;
    @SerializedName("device")
    public final String device;

    public LoginRequest(String email, String password, boolean remember) {
        this.email = email;
        this.password = password;
        this.remember = remember;
        this.device = "mobile";
    }
}
