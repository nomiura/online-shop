package domain.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal additionPrice;
}
