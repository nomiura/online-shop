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
        log.info("Creating order for account id: {}", request.getAccountId());
        Account account = accountRepository.findById(request.getAccountId())
                        .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        Cart cart = account.getCart();
        if(cart.getItems().isEmpty() ) {
            log.warn("Cart is empty for account id: {}", request.getAccountId());
           throw new CartEmptyException("Cart is empty");
       }
        if(account.getAccountType() == AccountType.INDIVIDUAL){
            for(CartItem item : cart.getItems()){
                if(item.getQuantity()>10) {
                    log.warn("Quantity limit exceeded for account id: {}", request.getAccountId());
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

        for(CartItem item : cart.getItems()){
            OrderItem orderItem = new OrderItem();
            Product product = item.getProduct();

            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOriginalPrice(product.getCurrentPrice());
            orderItem.setPriceAtPurchase(product.getEffectivePrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            price = price.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setPrice(price);
        order.setItems(orderItems);
        orderRepository.save(order);
        log.info("Order created with id: {}", order.getOrderId());
        cart.getItems().clear();
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> findByAccountId(Long accountId) {
       return orderRepository.findByCreatedBy_Id(accountId).stream()
               .map(orderMapper::toResponse)
               .toList();
    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!order.getOrderStatus().isCreated()) {
            log.warn("Order is not cancelled for order id: {}", orderId);
            throw new InvalidOrderStatusException("Отмена из данного статуса запрещена.");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order cancelled with id: {}", order.getOrderId());
        return orderMapper.toResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse updateDescription(Long orderId, UpdateDescriptionRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getOrderStatus().isTerminal()) {
            log.warn("Description is not updated for order id: {}", orderId);
            throw new InvalidOrderStatusException("Изменение комментария из данного статуса запрещено.");
        }
        order.setDescription(request.getDescription());
        log.info("Description updated with id: {}", order.getOrderId());

        return orderMapper.toResponse(order);
    }

}
