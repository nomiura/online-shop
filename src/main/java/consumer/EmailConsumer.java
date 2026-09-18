package consumer;


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

    //@KafkaListener - spring подписывает этот метод на топик, как в топике появляется новое сообщение --
    //метод вызывается автоматически
    @KafkaListener(topics = "order-created", groupId = "shop-group")
    public void handleOrderCreated(OrderCreatedEvent event) { //OrderCreatedEvent event) спринг сам десериализует
        //json обратно в объект через JsonDeserializer
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
