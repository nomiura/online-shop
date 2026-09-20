package domain.dto.response;



import domain.entity.ReviewStats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private Long id;
    private String name;
    private String description;
    private BigDecimal currentPrice;
    private Integer discountPercent;
    private BigDecimal effectivePrice;
    private String image;
    private boolean inStock;
    private ReviewStats reviewStats;
}
