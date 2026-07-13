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
        Account account = accountRepository.findById(request.getAccountId())
                        .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        Cart cart = account.getCart();
        if(cart.getItems().isEmpty() ) {
           throw new CartEmptyException("Cart is empty");
       }
        if(account.getAccountType() == AccountType.INDIVIDUAL){
            for(CartItem item : cart.getItems()){
                if(item.getQuantity()>10) {
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
        }
        order.setPrice(price);
        order.setItems(orderItems);
        orderRepository.save(order);
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
            throw new InvalidOrderStatusException("Отмена из данного статуса запрещена.");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
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

        return orderMapper.toResponse(order);
    }
}
