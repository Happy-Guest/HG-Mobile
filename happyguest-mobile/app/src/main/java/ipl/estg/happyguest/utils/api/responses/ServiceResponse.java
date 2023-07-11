package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Service;


public class ServiceResponse extends MessageResponse {

    @SerializedName("data")
    private final Service service;

    public ServiceResponse(String message, @Nullable Service service) {
        super(message);
        this.service = service;
    }

    @Nullable
    public Service getService() {
        return service;
    }
}
