package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("id")
    private final Long id;

    @SerializedName("stars")
    private final int stars;

    @Nullable
    @SerializedName("comment")
    private final String comment;

    @SerializedName("authorize")
    private final int authorize;

    @SerializedName("shared")
    private final int shared;

    @SerializedName("created_at")
    private final String createdAt;

    public Review(Long id, int stars, @Nullable String comment, int authorize, int shared, String createdAt) {
        this.id = id;
        this.stars = stars;
        this.comment = comment;
        this.authorize = authorize;
        this.shared = shared;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public int getStars() {
        return stars;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public int getAuthorize() {
        return authorize;
    }

    public int getShared() {
        return shared;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
