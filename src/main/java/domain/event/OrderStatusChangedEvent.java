package domain.event;

import domain.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusChangedEvent {
    private Long orderId;
    private OrderStatus orderStatus;
    private String accountEmail;
}
