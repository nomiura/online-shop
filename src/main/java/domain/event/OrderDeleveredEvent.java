package domain.event;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeleveredEvent {
    private Long orderId;
    private String phone;
    private Instant deleveredAt;
}
