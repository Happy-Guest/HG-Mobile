package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class UserResponse extends MessageResponse {

    @SerializedName("id")
    private final int id;
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
    @SerializedName("photo_url")
    private final String photoUrl;
    @SerializedName("user")
    private final UserResponse user;

    public UserResponse(int id, String name, String email, Long phone, String address, String birthDate, String photoUrl, UserResponse user, String message) {
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

    public Long getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public UserResponse getUser() {
        return user;
    }
}
