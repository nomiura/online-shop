package producer;

import domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j //автоматически создает поле private static final Logger log = LoggerFactory.getLogger(MyClass.class);
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-created"; //название топика

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Отправляем сообщение в Kafka: заказ{}", event.getOrderId());

        //отправляем в топик "order-created"
        //ключ - id заказа(чтобы заказ с одним id попадали в одну партицию)
        //значение - само сообщение/событие (автоматически конвертируется в json)
        //topic, Key: orderId, Value: OrderCreatedEvent (JSON)
        kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);

        log.info("Сообщение отправлено");
    }
}
