package domain.mapper;

import domain.dto.response.ReviewResponse;
import domain.entity.Review;

import java.util.Optional;

public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getRating(),
                review.getReviewContent(),
                review.getProduct()
        );
    }
}
