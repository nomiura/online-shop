package domain.dto.response;


import domain.entity.Product;
import domain.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponse {
    private String rating;
    private String reviewContent;
    private Product product;

}
