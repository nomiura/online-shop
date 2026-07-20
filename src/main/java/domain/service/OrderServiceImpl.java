package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateDescriptionRequest;
import domain.dto.response.OrderResponse;
import domain.entity.*;
import domain.exception.*;
import domain.mapper.OrderMapper;
import domain.repository.AccountRepository;
import domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    @Override
    public OrderResponse findById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating order with account Id: {}",request.getAccountId());
        Account account = accountRepository.findById(request.getAccountId())
                        .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Cart cart = account.getCart();
        if(cart.getItems().isEmpty() ) {
            log.warn("Order rejected: cart is empty, accountId={}", account.getId());
           throw new CartEmptyException("Cart is empty");
       }
        if(account.getAccountType() == AccountType.INDIVIDUAL){
            for(CartItem item : cart.getItems()){
                if(item.getQuantity()>10) {
                    log.warn("Order rejected: quantity limit exceeded, accountId={}, productId={}, qty={}",
                            account.getId(), item.getProduct().getProductId(), item.getQuantity());
                    throw new QuantityLimitExceededException("Quantity limit exceeded");
                }
            }
        }

        Order order = new Order();
        order.setCreatedBy(account);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setDescription(request.getDescription());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal price = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            BigDecimal effectivePrice = product.getEffectivePrice();

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOriginalPrice(product.getCurrentPrice());
            orderItem.setPriceAtPurchase(effectivePrice);
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            price = price.add(effectivePrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            log.debug("Order item: productId={}, qty={}, price={}",
                    product.getProductId(), item.getQuantity(), effectivePrice);
        }
        order.setPrice(price);
        order.setItems(orderItems);
        orderRepository.save(order);
        log.info("Order is created with ID {}", order.getOrderId());
        cart.getItems().clear();
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findByAccountId(Long accountId) {
        log.debug("Finding orders by account Id: {}", accountId);
       return orderRepository.findByCreatedBy_Id(accountId).stream()
               .map(orderMapper::toResponse)
               .toList();

    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!order.getOrderStatus().isCreated()) {
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

        if(order.getOrderStatus().isTerminal()) {
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

        Account account = oldOrder.getCreatedBy();

        if (account.getAccountType() == AccountType.INDIVIDUAL) {
            for (OrderItem item : oldOrder.getItems()) {
                if (item.getQuantity() > 10) {
                    log.warn("Recreate rejected: quantity limit exceeded, orderId={}, productId={}, qty={}",
                            orderId, item.getProduct().getProductId(), item.getQuantity());
                    throw new QuantityLimitExceededException("Quantity limit exceeded");
                }
            }
        }

        for(OrderItem item : oldOrder.getItems()){
            Product product = item.getProduct();
            Integer available = product.getQuantityAvailable();
            if(available == null || available < item.getQuantity()) {
                log.warn("Recreate rejected: insufficient stock, orderId={}, productId={}, requested={}, available={}",
                        orderId, product.getProductId(), item.getQuantity(), available);
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                                + ". Requested: " + item.getQuantity() + ", available: " + available);
            }
        }
        // повторить можно заказ в любом статусе — осознанно без проверки

         Order newOrder = new Order();
         newOrder.setOrderStatus(OrderStatus.CREATED);
         newOrder.setCreatedBy(oldOrder.getCreatedBy());

         List <OrderItem> newItems = new ArrayList<>();
         BigDecimal price = BigDecimal.ZERO;



         for(OrderItem oldItem : oldOrder.getItems()) {
             Product product = oldItem.getProduct();
             BigDecimal effectivePrice = product.getEffectivePrice();

             OrderItem  newItem = new OrderItem();
             newItem.setProduct(product);
             newItem.setQuantity(oldItem.getQuantity());
             newItem.setOriginalPrice(product.getCurrentPrice());
             newItem.setPriceAtPurchase(effectivePrice);
             newItem.setOrder(newOrder);
             newItems.add(newItem);

             price = price.add(effectivePrice.multiply(BigDecimal.valueOf(oldItem.getQuantity())));
         }

         newOrder.setItems(newItems);
         newOrder.setPrice(price);

        orderRepository.save(newOrder);
        log.info("Order {} recreated as new order {}", orderId, newOrder.getOrderId());

        return orderMapper.toResponse(newOrder);
    }
}
