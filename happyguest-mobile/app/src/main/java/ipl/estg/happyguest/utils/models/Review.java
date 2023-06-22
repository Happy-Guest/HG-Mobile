package ipl.estg.happyguest.utils.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("id")
    private Long id;
    @SerializedName("stars")
    private int stars;
    @Nullable
    @SerializedName("comment")
    private String comment;
    @SerializedName("autorize")
    private int autorize;
    @SerializedName("shared")
    private int shared;
    @SerializedName("created_at")
    private String createdAt;

    public Review(Long id, int stars, @Nullable String comment, int autorize, int shared, String createdAt) {
        this.id = id;
        this.stars = stars;
        this.comment = comment;
        this.autorize = autorize;
        this.shared = shared;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    public int getAutorize() {
        return autorize;
    }

    public void setAutorize(int autorize) {
        this.autorize = autorize;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
