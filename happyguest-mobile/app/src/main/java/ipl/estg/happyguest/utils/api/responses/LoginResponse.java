package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("expires_at")
    public int expiresAt;

    @SerializedName("message")
    public String message;
}
