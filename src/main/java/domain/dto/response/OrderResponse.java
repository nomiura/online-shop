package domain.dto.response;

import domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String description;
    private OrderStatus orderStatus;
    private BigDecimal price;
}
