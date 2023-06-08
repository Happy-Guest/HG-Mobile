package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {

    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;
    @SerializedName("phone")
    private final Long phone;
    @SerializedName("address")
    private final String address;
    @SerializedName("birth_date")
    private final String birthDate;
    @SerializedName("photoBase64")
    private final String photoBase64;

    public UpdateUserRequest(String name, String email, Long phone, String address, String birthDate, String photoBase64) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.photoBase64 = photoBase64;
    }
}
