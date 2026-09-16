package domain.dto.request;

import domain.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {
    private Long accountId;
    private Long productId;
    private String reviewContent;
    private BigDecimal rating;
    private List<String> imageUrls;




}
