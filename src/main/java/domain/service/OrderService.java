package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateDescriptionRequest;
import domain.dto.response.OrderResponse;
import domain.entity.OrderItem;

import java.util.List;


public interface OrderService {
    OrderResponse findById(Long orderId);
    List<OrderResponse> findByAccountId(Long accountId);
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse cancelOrder(Long orderId);
    OrderResponse updateDescription(Long orderId, UpdateDescriptionRequest request);
    OrderResponse recreateOrder(Long orderId);
    OrderResponse markAsDelevered(Long orderId);
}
