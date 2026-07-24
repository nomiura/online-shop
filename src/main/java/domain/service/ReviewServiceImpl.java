package domain.service;

import domain.dto.response.ReviewResponse;
import domain.entity.Review;
import domain.exception.ReviewNotFoundException;
import domain.mapper.ReviewMapper;
import domain.repository.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class ReviewServiceImpl implements ReviewService{
    private ReviewRepository reviewRepository;
    private ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    @Override
    Optional<ReviewResponse> findById(Review review, Long accountId) {
        reviewRepository.findById(review,accountId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return reviewMapper.toResponse(review);
    }
}
