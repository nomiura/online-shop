package domain.service;

import domain.dto.request.CreateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.entity.Account;
import domain.entity.Product;
import domain.entity.Review;
import domain.exception.AccountNotFoundException;
import domain.exception.ProductNotFoundException;
import domain.exception.ReviewNotFoundException;
import domain.mapper.ReviewMapper;
import domain.repository.AccountRepository;
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
public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    @Override
    public ReviewResponse findById(Long accountId) {
        Review findableReview = reviewRepository.findById(accountId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        return reviewMapper.toResponse(findableReview);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponse> findByProduct(Long productId) {
        return reviewRepository.findByProduct_ProductIdOrderByCreatedDateDesc(productId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse createReview(CreateReviewRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"+ request.getAccountId()));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.getProductId()));

        Review newReview = reviewMapper.toEntity(request, account, product);

        Review savedReview = reviewRepository.save(newReview);

        log.info("Review created: product={}, reviewContent={}",savedReview.getProduct(), savedReview.getReviewContent());

        return reviewMapper.toResponse(savedReview);

    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + reviewId));

        reviewRepository.delete(review);
        log.info("Review  was deleted: id={}", reviewId);
    }
}
