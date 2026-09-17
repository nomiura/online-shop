package domain.service;

import domain.dto.request.CreateReviewRequest;
import domain.dto.request.UpdateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.entity.Account;
import domain.entity.Product;
import domain.entity.Review;
import domain.exception.ProductNotFoundException;
import domain.exception.ReviewAccessDeniedException;
import domain.exception.ReviewAlreadyExistsException;
import domain.exception.ReviewNotFoundException;
import domain.mapper.ReviewMapper;
import domain.repository.ProductRepository;
import domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    @Override
    public ReviewResponse findById(Long reviewId) {
        Review findableReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return reviewMapper.toResponse(findableReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> findMyReviews(Account account) {
        return reviewRepository.findByAccount_IdOrderByCreatedDateDesc(account.getId())
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> findByProduct(Long productId) {
        return reviewRepository.findByProduct_ProductIdOrderByCreatedDateDesc(productId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse createReview(Account currentAccount, Long productId, CreateReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found" + productId));

        if (reviewRepository.existsByAccount_IdAndProduct_ProductId(currentAccount.getId(), product.getProductId())) {
            throw new ReviewAlreadyExistsException(
                    "Account with id " + currentAccount.getEmail() + " has already reviewed product with id " + product.getName());
        }
            Review newReview = reviewMapper.toEntity(request, currentAccount, product);
            Review savedReview = reviewRepository.save(newReview);

            log.info("Review created for product={}, reviewContent={}", savedReview.getProduct(), savedReview.getReviewContent());
            return reviewMapper.toResponse(savedReview);

    }
    @Transactional
    @Override
    public ReviewResponse updateReview(Account account, Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found: id=" + reviewId));

        if (!review.getAccount().getId().equals(account.getId())) {
            throw new ReviewAccessDeniedException(
                    "Account " + account.getId() + " is not the owner of review " + reviewId);
        }

        if (request.getReviewContent() != null) review.setReviewContent(request.getReviewContent());
        if (request.getRating() != null) review.setRating(request.getRating());

        log.info("Review updated: id={}, account={}", reviewId, account.getId());
        return reviewMapper.toResponse(review);
    }

    @Transactional
    @Override
    public void deleteReview(Account currentAccount, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found: id=" + reviewId));

        if (!review.getAccount().getId().equals(currentAccount.getId())) {
            throw new ReviewAccessDeniedException("Account " + currentAccount.getEmail() + " is not the owner of review ");

        }

        reviewRepository.delete(review);
        log.info("Review deleted: account={}, review={}", currentAccount.getId(), reviewId);
    }
}
