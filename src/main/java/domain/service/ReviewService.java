package domain.service;


import domain.dto.request.CreateReviewRequest;
import domain.dto.request.UpdateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.entity.Account;

import java.util.List;

public interface ReviewService {

    ReviewResponse findById(Long reviewId);
    List<ReviewResponse> findByProduct(Long productID);
    List<ReviewResponse> findMyReviews(Account account);
    ReviewResponse createReview(Account currentAccount, Long productId,CreateReviewRequest request);
    ReviewResponse updateReview(Account account, Long productId, UpdateReviewRequest request);
    void deleteReview(Account account,Long reviewId);

}
