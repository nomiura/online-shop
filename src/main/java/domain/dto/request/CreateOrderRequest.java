package domain.dto.request;

import domain.entity.Account;
import domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    private Long accountId;
    private Long orderId;
    private OrderStatus orderStatus;
    private String description;
    private BigDecimal price;
}
