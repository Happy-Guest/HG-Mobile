package ipl.estg.happyguest.utils.api.responses;

import java.util.ArrayList;

import ipl.estg.happyguest.utils.models.Meta;
import ipl.estg.happyguest.utils.models.Review;

public class ReviewsResponse extends PaginationResponse<Review> {

    public ReviewsResponse(ArrayList<Review> reviews, Meta meta) {
        super(reviews, meta);
    }
}
