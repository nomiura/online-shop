package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateStatusOrderRequest;
import domain.entity.Order;

import java.util.List;
import java.util.Optional;


public interface OrderService {
    Optional<Order> findById(Long id);
    List<Order> findByAccount(Long accountId);
    Order createOrder(CreateOrderRequest request);
    Order updateOrderStatus(Long id,UpdateStatusOrderRequest request);
    Order updateComment(Long id, String comment);

}
