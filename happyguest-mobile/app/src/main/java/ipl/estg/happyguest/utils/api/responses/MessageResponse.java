package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("message")
    private final String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
