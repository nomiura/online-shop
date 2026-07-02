package domain.mapper;

import domain.dto.request.CreateOrderRequest;
import domain.dto.response.OrderResponse;
import domain.entity.Order;
import org.springframework.stereotype.Component;


@Component
public class OrderMapper {

    public Order toEntity(CreateOrderRequest request) {
        if (request == null) return null;
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setOrderStatus(request.getOrderStatus());
        order.setDescription(request.getDescription());
        order.setPrice(request.getPrice());
        return  order;
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;
        return new OrderResponse(
                order.getOrderId(),
                order.getDescription(),
                order.getOrderStatus(),
                order.getPrice()
        );
    }
}
