package domain.producer;

import domain.event.OrderCreatedEvent;
import domain.event.OrderDeleveredEvent;
import domain.event.ProductOutOfStockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j //автоматически создает поле private static final Logger log = LoggerFactory.getLogger(MyClass.class);
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String CREATED_TOPIC = "order-created"; //название топика
    private static final String DELEVERED_TOPIC = "order-delevered";
    private static final String OUT_OF_STOCK_TOPIC = "product-out-of-stock";

    public void sendOrderDeliveredEvent(OrderDeleveredEvent event) {
        log.info("Отправляем сообщение о доставке в Kafka: заказ{}", event.getOrderId());

        kafkaTemplate.send(DELEVERED_TOPIC, String.valueOf(event.getOrderId()), event);

        log.info("Сообщение о доставке отправлено");
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Отправляем сообщение в Kafka: заказ{}", event.getOrderId());

        //отправляем в топик "order-created"
        //ключ - id заказа(чтобы заказ с одним id попадали в одну партицию)
        //значение - само сообщение/событие (автоматически конвертируется в json)
        //topic, Key: orderId, Value: OrderCreatedEvent (JSON)
        kafkaTemplate.send(CREATED_TOPIC, String.valueOf(event.getOrderId()), event);

        log.info("Сообщение отправлено");
    }

    public void sendQuantityEvent(ProductOutOfStockEvent event) {
        log.info("Отправляю сообщение о количестве товара в Kafka: товар{}",event.getProductId());

        kafkaTemplate.send(OUT_OF_STOCK_TOPIC, String.valueOf(event.getProductId()), event);
    }
}
