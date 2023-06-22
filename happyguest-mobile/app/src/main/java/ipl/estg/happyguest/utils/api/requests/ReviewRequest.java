package ipl.estg.happyguest.utils.api.requests;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {

    @Nullable
    @SerializedName("user_id")
    private final Integer user_id;
    @SerializedName("stars")
    private final int stars;
    @SerializedName("comment")
    private final String comment;
    @SerializedName("autorize")
    private final String autorize;

    public ReviewRequest(@Nullable Integer user_id, int stars, String comment, String autorize) {
        this.user_id = user_id;
        this.stars = stars;
        this.comment = comment;
        this.autorize = autorize;
    }
}
