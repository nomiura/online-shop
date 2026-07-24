package domain.service;


import domain.dto.response.ReviewResponse;
import domain.entity.Review;

import java.util.Optional;

public interface ReviewService {

    Optional<ReviewResponse> findById(Review review, Long accountId);
}
