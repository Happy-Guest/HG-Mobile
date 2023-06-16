package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class CodeRequest {

    @SerializedName("user_id")
    private final int user_id;
    @SerializedName("code")
    private final String code;

    public CodeRequest(int user_id, String code) {
        this.user_id = user_id;
        this.code = code;
    }
}
