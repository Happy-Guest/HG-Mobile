package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends MessageResponse {

    @SerializedName("access_token")
    private final String accessToken;

    public LoginResponse(String accessToken, String message) {
        super(message);
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
