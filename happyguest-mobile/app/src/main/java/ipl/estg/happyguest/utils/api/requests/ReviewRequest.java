package ipl.estg.happyguest.utils.api.requests;

import com.google.gson.annotations.SerializedName;

public class ReviewRequest {

    @SerializedName("user_id")
    private final int user_id;
    @SerializedName("stars")
    private final int stars;
    @SerializedName("comment")
    private final String comment;
    @SerializedName("autorize")
    private final String autorize;

    public ReviewRequest(int user_id, int stars, String comment, String autorize) {
        this.user_id = user_id;
        this.stars = stars;
        this.comment = comment;
        this.autorize = autorize;
    }
}
