package domain.dto.response;


import domain.entity.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String username;
    private BigDecimal rating;
    private String reviewContent;
    private Product product;

}
