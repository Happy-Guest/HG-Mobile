package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    private final String accessToken;
    @SerializedName("message")
    private final String message;

    public LoginResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getMessage() {
        return message;
    }
}
