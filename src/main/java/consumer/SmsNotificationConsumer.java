package consumer;


import domain.event.OrderDeleveredEvent;
import domain.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsNotificationConsumer {
    private final SmsService smsService;

    @KafkaListener(topics = "order-delevered", groupId = "order-group")
    public void handleOrderDelevered(OrderDeleveredEvent event) {
        log.info("Получаю сообщение из KafkaL о доставке: заказ{}", event.getOrderId());

        try {
            smsService.sendSms(event.getPhone(),
                    "Заказ #" + event.getOrderId() + " доставлен");
        } catch (Exception e) {
            log.error("Ошибка при отправке смс для заказа{}", event.getOrderId(), e);
        }
    }
}

