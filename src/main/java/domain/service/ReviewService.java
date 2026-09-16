package domain.service;


import domain.dto.request.CreateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.entity.Review;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewService {

    ReviewResponse findById(Long accountId);

    List<ReviewResponse> findByProduct(Long productID);

    ReviewResponse createReview(CreateReviewRequest request);

    void deleteReview(Long Id);

}
