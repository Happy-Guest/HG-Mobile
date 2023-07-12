package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusRequest {

    @SerializedName("status")
    private final String status;

    public UpdateStatusRequest(String status) {
        this.status = status;
    }
}
