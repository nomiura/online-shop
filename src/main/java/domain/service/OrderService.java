package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateDescriptionRequest;
import domain.dto.response.OrderResponse;

import java.util.List;


public interface OrderService {
    OrderResponse findById(Long orderId);
    List<OrderResponse> findByAccountId(Long accountId);
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse cancelOrder(Long id);
    OrderResponse updateComment(Long id, UpdateDescriptionRequest request);

}
