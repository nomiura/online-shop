package consumer;

import domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsConsumer {

    private final StatisticsService statisticsService;

    @KafkaListener(topics = "order-created", groupId = "statistics-group")
    public void handleOrderForStatistics(OrderCreatedEvent event) {
        log.info("Обновляем статистикку для заказа {}", event.getOrderId());

        try {
            statisticsService.recordOrder(event.getOrderId(), event.getTotalAmount());
            log.info("Статистика обновлена");
        } catch (Exception e) {
            log.error("Ошибка статистики: ", e);
        }
    }
}
