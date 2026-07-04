package domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemResponse {
    private String productName;       // или Long productId — на твой выбор
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
