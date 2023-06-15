package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class UserResponse extends MessageResponse {

    @SerializedName("id")
    private final int id;
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
    @SerializedName("photo_url")
    private final String photoUrl;
    @SerializedName("user")
    private final UserResponse user;

    public UserResponse(int id, String name, String email, @Nullable Long phone, @Nullable String address, @Nullable String birthDate, @Nullable String photoUrl, UserResponse user, String message) {
        super(message);
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.birthDate = birthDate;
        this.photoUrl = photoUrl;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Nullable
    public Long getPhone() {
        return phone;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    @Nullable
    public String getBirthDate() {
        return birthDate;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    public UserResponse getUser() {
        return user;
    }
}
