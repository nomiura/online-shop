package domain.mapper;

import domain.dto.response.OrderItemResponse;
import domain.dto.response.OrderResponse;
import domain.entity.Order;
import domain.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class OrderMapper {

    public OrderItemResponse toItemResponse(OrderItem item) {
        if(item == null) return null;
        return  new OrderItemResponse(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtPurchase()
        );
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();
        return new OrderResponse(
                order.getOrderId(),
                order.getDescription(),
                order.getOrderStatus(),
                order.getPrice(),
                itemResponses
        );
    }
}
