package domain.consumer;


import domain.event.OrderCreatedEvent;
import domain.event.OrderStatusChangedEvent;
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
    private static final String CREATED_TOPIC = "order-created";
    private static final String STATUS_CHANGED_TOPIC = "order-status-changed";

    //@KafkaListener - spring подписывает этот метод на топик, как в топике появляется новое сообщение --
    //метод вызывается автоматически
    @KafkaListener(topics = CREATED_TOPIC, groupId = "email-notification-group")
    public void handleOrderCreated(OrderCreatedEvent event) { //OrderCreatedEvent event спринг сам десериализует
        //json обратно в объект через JsonDeserializer
        log.info("Получаю сообщение из KafkaL: заказ{}", event.getOrderId());

        try {
            //отправляем email, это будет асинхронно
            emailService.sendOrderConfirmationHtml(
                    event.getAccountEmail(),
                    event.getOrderId(),
                    event.getTotalAmount()
            );

            log.info("Email отправлен для заказа{}", event.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка при отправке email для заказа{}", event.getOrderId(), e);
        }
    }

    @KafkaListener(topics = STATUS_CHANGED_TOPIC, groupId = "email-notification-group")
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Получаю сообщение из KafkaL: заказ{}", event.getOrderId());

        try{
            emailService.sendOrderStatusChangedConfirmationHtml(event.getAccountEmail(), event.getOrderId(), event.getOrderStatus());
        }catch(Exception e) {
            log.error("Ошибка при отправке email для заказа{}", event.getOrderId(), e);
        }
    }
}
