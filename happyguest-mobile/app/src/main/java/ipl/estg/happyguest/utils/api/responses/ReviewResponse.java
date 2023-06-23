package ipl.estg.happyguest.utils.api.responses;

import com.google.gson.annotations.SerializedName;

import ipl.estg.happyguest.utils.models.Review;

public class ReviewResponse extends MessageResponse {

    @SerializedName("data")
    private final Review review;

    public ReviewResponse(String message, Review review) {
        super(message);
        this.review = review;
    }

    public Review getReview() {
        return review;
    }
}
