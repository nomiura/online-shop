package domain.listeners;


import domain.event.OrderCreatedEvent;
import domain.event.ProductOutOfStockEvent;
import domain.producer.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderEventProducer eventProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated (OrderCreatedEvent event) {
        eventProducer.sendOrderCreatedEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductOutOfStock(ProductOutOfStockEvent event) {
        eventProducer.sendQuantityEvent(event);
    }
}
