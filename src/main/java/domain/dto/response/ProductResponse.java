package domain.dto.response;



import lombok.AllArgsConstructor;
import lombok.Data;


import java.math.BigDecimal;

@Data
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
