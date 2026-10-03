package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateDescriptionRequest;
import domain.dto.response.OrderResponse;
import domain.entity.*;
import domain.event.OrderCreatedEvent;
import domain.event.OrderDeleveredEvent;
import domain.event.ProductOutOfStockEvent;
import domain.exception.*;
import domain.mapper.OrderMapper;
import domain.producer.OrderEventProducer;
import domain.repository.AccountRepository;
import domain.repository.OrderRepository;
import domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    @Override
    public OrderResponse findById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toResponse(order);
    }

    //Метод, который позволяет не дублировать код сборки заказа, а сразу строить общий Order для create и recreate
    private Order buildOrder(Account account, List<OrderLine> orderLines) {
        List<OrderLine> lines = orderLines.stream()
                .sorted(Comparator.comparing(OrderLine::productId))
                .toList();

        Order order = new Order();

        List<OrderItem> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderLine l : lines) {
            Product product = productRepository.findById(l.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
            int updated = productRepository.updateStock(product.getProductId(), l.quantity());
            if (updated == 0) {
                Integer qty = productRepository.getQuantityAvailableById(l.productId());
                throw new InsufficientStockException("Недостаточно товара: " + product.getName()
                        + ". Доступно: " + qty
                        + ", запрошено: " + l.quantity());
            }
            if(productRepository.getQuantityAvailableById(l.productId()) == 0) {
                publisher.publishEvent( new ProductOutOfStockEvent(product.getProductId(),
                        product.getName(),
                        Instant.now(),
                        l.quantity()
                        ));

            }
            BigDecimal effectivePrice = product.getEffectivePrice();
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(l.quantity());
            orderItem.setOriginalPrice(product.getCurrentPrice());
            orderItem.setPriceAtPurchase(effectivePrice);
            orderItem.setOrder(order);

            log.debug("Order item: productId={}, qty={}, price={}",
                    product.getProductId(), orderItem.getQuantity(), effectivePrice);

            items.add(orderItem);

            total = total.add(effectivePrice.multiply(BigDecimal.valueOf(l.quantity())));
        }
        order.setAccount(account);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setItems(items);
        order.setPrice(total);
        return order;

    }

    @Transactional
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating order with account Id: {}", request.getAccountId());
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Cart cart = account.getCart();
        if (cart.getItems().isEmpty()) {
            log.warn("Order rejected: cart is empty, accountId={}", account.getId());
            throw new CartEmptyException("Cart is empty");
        }

        if (account.getAccountType() == AccountType.INDIVIDUAL) {
            for (CartItem item : cart.getItems()) {
                if (item.getQuantity() > 10) {
                    log.warn("Order rejected: quantity limit exceeded, accountId={}, productId={}, qty={}",
                            account.getId(), item.getProduct().getProductId(), item.getQuantity());
                    throw new QuantityLimitExceededException("Quantity limit exceeded");
                }
            }
        }

        List<OrderLine> lines = account.getCart().getItems()
                .stream()
                .map(l -> new OrderLine(l.getProduct().getProductId(), l.getQuantity()))
                .toList();

        Order order = buildOrder(account, lines);

        order.setDescription(request.getComment());

        orderRepository.save(order);
        log.info("Order is created with ID {}", order.getOrderId());
        cart.getItems().clear();

        //отправляем сообщение в кафка (не блокирует)
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getOrderId(),
                order.getAccount().getId(),
                order.getPrice(),
                order.getAccount().getEmail(),
                order.getAccount().getPhone(),
                order.getCreatedAt()
        );
        publisher.publishEvent(event);

        //возвращаем ответ клиенту (не ждем отправку email!)
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findByAccountId(Long accountId) {
        log.debug("Finding orders by account Id: {}", accountId);
        return orderRepository.findByAccountId(accountId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getOrderStatus().isCreated()) {
            log.warn("Cancel rejected: orderId={}, status={}", orderId, order.getOrderStatus());
            throw new InvalidOrderStatusException("Отмена из данного статуса запрещена." + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        log.info("Order is cancelled with ID {}", order.getOrderId());
        return orderMapper.toResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse updateDescription(Long orderId, UpdateDescriptionRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus().isTerminal()) {
            throw new InvalidOrderStatusException("Изменение комментария из данного статуса запрещено.");
        }
        order.setDescription(request.getDescription());
        log.info("Order's description has been updated with ID {}", order.getOrderId());
        return orderMapper.toResponse(order);

    }

    @Transactional
    @Override
    public OrderResponse recreateOrder(Long orderId) {
        Order oldOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Account account = oldOrder.getAccount();

        if (account.getAccountType() == AccountType.INDIVIDUAL) {
            for (OrderItem item : oldOrder.getItems()) {
                if (item.getQuantity() > 10) {
                    log.warn("Recreate rejected: quantity limit exceeded, orderId={}, productId={}, qty={}",
                            orderId, item.getProduct().getProductId(), item.getQuantity());
                    throw new QuantityLimitExceededException("Quantity limit exceeded");
                }
            }
        }

        List<OrderLine> lines = oldOrder.getItems()
                .stream()
                .map(i -> new OrderLine(i.getProduct().getProductId(), i.getQuantity()))
                .toList();

        // повторить можно заказ в любом статусе — осознанно без проверки
        Order newOrder = buildOrder(account, lines);

        orderRepository.save(newOrder);
        log.info("Order {} recreated as new order {}", orderId, newOrder.getOrderId());

        //отправляем сообщение в кафка (не блокирует)
        OrderCreatedEvent event = new OrderCreatedEvent(
                newOrder.getOrderId(),
                newOrder.getAccount().getId(),
                newOrder.getPrice(),
                newOrder.getAccount().getEmail(),
                newOrder.getAccount().getPhone(),
                newOrder.getCreatedAt()
        );
        publisher.publishEvent(event);

        return orderMapper.toResponse(newOrder);
    }


    @Transactional
    @Override
    public OrderResponse markAsDelevered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getOrderStatus() == OrderStatus.DELEVERED) {
            return orderMapper.toResponse(order);
        }

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStatusException("Order status is not valid");
        }
        order.setOrderStatus(OrderStatus.DELEVERED);
        log.info("Order marked as delevered: id={}", orderId);
        order.setDeleveredAt(Instant.now());

        OrderDeleveredEvent event = new OrderDeleveredEvent
                (order.getOrderId(),
                        order.getAccount().getPhone(),
                        order.getDeleveredAt()
                );
        publisher.publishEvent(event);
        return orderMapper.toResponse(order);
    }
}
