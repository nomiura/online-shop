package domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class Order {
    @Column(unique = true)
    private Long orderId;

    @Column(unique = true)
    private User createdBy;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}

