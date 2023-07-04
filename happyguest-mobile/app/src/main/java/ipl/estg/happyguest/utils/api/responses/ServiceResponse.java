package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


public class ServiceResponse extends MessageResponse {

    @SerializedName("id")
    private final Long id;
    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;
    @Nullable
    @SerializedName("phone")
    private final Long phone;

    public ServiceResponse(String message, Long id, String name, String email, @Nullable Long phone) {
        super(message);
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
