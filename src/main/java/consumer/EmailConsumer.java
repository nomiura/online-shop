package consumer;

import domain.entity.Order;
import domain.event.OrderCreatedEvent;
import domain.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "order-created", groupId = "shop-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Получаю сообщение из KafkaL: заказ{}", event.getOrderId());

        try {
            //отправляем email, это будет асинхронно
            emailService.sendOrderConfirmation(
                    event.getAccountEmail(),
                    event.getOrderId(),
                    event.getTotalAmount()
            );

            log.info("Email отправлен для заказа{}", event.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка при отправке email для заказа{}", event.getOrderId(), e);
        }
    }
}
