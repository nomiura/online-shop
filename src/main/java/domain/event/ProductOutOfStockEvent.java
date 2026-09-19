package domain.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOutOfStockEvent {
    private Long productId;
    private String productName;
    private Instant occurredAt;
    private Integer lastOrderedQty;

}
