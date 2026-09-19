package domain.event;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;



//простой POJO (Plain Old Java Object) - посылка, которую кидаем в kafka
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor //нужен для десериализации (jackson создает объект без аргументов, потом заполняет поле)
@AllArgsConstructor //удобно создавать объект одной строкой
public class OrderCreatedEvent {
    private Long orderId;
    private Long accountId;
    private BigDecimal totalAmount;
    private String accountEmail;
    private String phone;
    private Instant createdAt;
}
