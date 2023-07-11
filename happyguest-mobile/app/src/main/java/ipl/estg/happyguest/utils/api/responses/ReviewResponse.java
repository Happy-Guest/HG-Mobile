package ipl.estg.happyguest.utils.api.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Review;

public class ReviewResponse extends MessageResponse {

    @SerializedName("data")
    private final Review review;

    public ReviewResponse(String message, @Nullable Review review) {
        super(message);
        this.review = review;
    }

    @Nullable
    public Review getReview() {
        return review;
    }
}
