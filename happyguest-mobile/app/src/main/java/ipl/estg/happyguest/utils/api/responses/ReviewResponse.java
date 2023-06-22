package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ReviewResponse extends MessageResponse{

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

    public ReviewResponse(Long id, int stars, @Nullable String comment, int autorize, int shared, String createdAt,String message) {
        super(message);
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

    @Nullable
    public String getComment() {
        return comment;
    }

    public int getAutorize() {
        return autorize;
    }

    public int getShared() {
        return shared;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
