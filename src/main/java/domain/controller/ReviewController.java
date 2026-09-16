package domain.controller;


import domain.dto.request.CreateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewsByAccount(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.findById(reviewId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProduct(productId));
    }

    @PostMapping("/review")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest reviewRequest) {
        ReviewResponse created = reviewService.createReview(reviewRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview (@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
