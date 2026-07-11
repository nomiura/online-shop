package domain.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponseDto {
    private Long cartId;
    private List<CartItemResponse> items;
    private Integer totalQuantity;
    private BigDecimal totalPrice; //итоговая цена
    private BigDecimal subtotalPrice; //цена без скидок
    private BigDecimal discountPrice; //скидка
    private String promoCode;
}
