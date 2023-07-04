package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {

    @Nullable
    @SerializedName("user_id")
    private final Long user_id;
    @SerializedName("stars")
    private final int stars;
    @Nullable
    @SerializedName("comment")
    private final String comment;
    @SerializedName("authorize")
    private final String authorize;

    public ReviewRequest(@Nullable Long user_id, int stars, @Nullable String comment, String authorize) {
        this.user_id = user_id;
        this.stars = stars;
        this.comment = comment;
        this.authorize = authorize;
    }
}
