package domain.mapper;

import domain.dto.request.CreateReviewRequest;
import domain.dto.response.ReviewResponse;
import domain.entity.Account;
import domain.entity.Product;
import domain.entity.Review;
import org.springframework.stereotype.Component;


@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if(review == null) return null;
        String login = review.getAccount() != null ? review.getAccount().getEmail() : "Аноним";
        return new ReviewResponse(
                login,
                review.getRating(),
                review.getReviewContent(),
                review.getProduct()
        );
    }
    public Review toEntity(CreateReviewRequest request,Account account, Product product) {
        Review review = new Review();
        review.setAccount(account);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setReviewContent(request.getReviewContent());

        return review;
    }
}
