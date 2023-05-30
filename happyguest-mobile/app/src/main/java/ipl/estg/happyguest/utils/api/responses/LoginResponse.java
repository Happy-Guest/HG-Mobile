package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;

public class LoginResponse {

    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("message")
    public String message;

    public LoginResponse(ResponseBody body) {
        accessToken = body.toString();
        tokenType = body.toString();
        message = body.toString();
    }
}
