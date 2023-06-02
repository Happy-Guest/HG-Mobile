package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("id")
    private final int id;
    @SerializedName("name")
    private final String name;
    @SerializedName("email")
    private final String email;
    @SerializedName("phone")
    private final Long phone;
    @SerializedName("photo_url")
    private final String photoUrl;

    public UserResponse(int id, String name, String email, Long phone, String photoUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.photoUrl = photoUrl;
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

    public String getPhotoUrl() {
        return photoUrl;
    }
}
