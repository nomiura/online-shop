package domain.dto.response;


import domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal currentPrice;
    private Integer discountPercent;
    private BigDecimal effectivePrice;
    private String image;
    private boolean inStock;
}
