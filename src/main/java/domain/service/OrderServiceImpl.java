package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateStatusOrderRequest;
import domain.entity.*;
import domain.exception.AccountNotFoundException;
import domain.exception.CartEmptyException;
import domain.exception.QuantityLimitExceededException;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public Optional<Order> findById(Long id) {
        return  orderRepository.findById(id) ;
    }

    @Transactional
    @Override
    public Order createOrder(CreateOrderRequest request) {
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

        for(CartItem item : cart.getItems()){
            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(item.getPriceAtAdd());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            price = price.add(item.getPriceAtAdd().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setPrice(price);
        order.setItems(orderItems);
        orderRepository.save(order);
        cart.getItems().clear();
        return order;
    }

    @Transactional
    @Override
    public List<Order> findByAccount(Long accountId) {
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public Order updateOrderStatus(Long id, UpdateStatusOrderRequest request) {
        return null;
    }

    @Transactional
    @Override
    public Order updateComment(Long id, String comment) {
        return null;
    }
}
