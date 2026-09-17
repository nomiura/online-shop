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
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.findByProduct(productId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @CurrentAccount Account account,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(account, productId, request));
    }
}