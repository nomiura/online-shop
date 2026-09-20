package domain.consumer;

import domain.event.OrderCreatedEvent;
import domain.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsConsumer {

    private final StatisticsService statisticsService;

    @KafkaListener(topics = "order-created", groupId = "statistics-group") //groupId = "statistics-group" —
    // отличается от shop-group у EmailConsumer. Поэтому оба получат сообщение.
    public void handleOrderForStatistics(OrderCreatedEvent event) {
        log.info("Обновляем статистику для заказа {}", event.getOrderId());

        try {
            statisticsService.recordOrder(
                    event.getOrderId(),
                    event.getAccountId(),
                    event.getTotalAmount()
            );

            log.info("Статистика обновлена для заказа {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка статистики для заказа {}: ", event.getOrderId(), e);
        }
    }
}
