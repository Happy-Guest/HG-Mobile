package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class HasCodesResponse extends MessageResponse {

    @SerializedName("validCode")
    private final boolean hasCodes;

    public HasCodesResponse(String message, boolean hasCodes) {
        super(message);
        this.hasCodes = hasCodes;
    }

    public boolean hasCodes() {
        return hasCodes;
    }
}
