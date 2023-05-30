package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("remember")
    public boolean remember;
    @SerializedName("device")
    public String device;

    public LoginRequest(String email, String password, boolean remember) {
        this.email = email;
        this.password = password;
        this.remember = remember;
        this.device = "mobile";
    }
}
