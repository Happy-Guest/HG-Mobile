package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {

    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;
    @Nullable
    @SerializedName("phone")
    private final Long phone;
    @Nullable
    @SerializedName("address")
    private final String address;
    @Nullable
    @SerializedName("birth_date")
    private final String birthDate;
    @Nullable
    @SerializedName("photoBase64")
    private final String photoBase64;

    public UpdateUserRequest(String name, String email, @Nullable Long phone, @Nullable String address, @Nullable String birthDate, @Nullable String photoBase64) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.photoBase64 = photoBase64;
    }
}
