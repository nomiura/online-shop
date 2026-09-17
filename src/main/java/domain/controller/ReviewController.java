package domain.controller;


import domain.annotation.CurrentAccount;
import domain.dto.request.CreateReviewRequest;
import domain.dto.request.UpdateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.entity.Account;
import domain.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@CurrentAccount Account account) {
        return ResponseEntity.ok(reviewService.findMyReviews(account));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.findById(reviewId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @CurrentAccount Account account,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(account, reviewId, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @CurrentAccount Account account,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(account, reviewId);
        return ResponseEntity.noContent().build();
    }

}
