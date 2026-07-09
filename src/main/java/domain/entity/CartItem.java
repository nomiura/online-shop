package domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    //количество конкретного товара
    @Column(nullable = false)
    private Integer quantity = 0;

    //цены
    @Column(name = "price_at_addition", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAddition; //цена на момент добавления в корзину

    @Column(name = "original_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice; //старая цена (для отображения скидки)

    //доп инфа по товару
    @Column(name = "comments")
    private String comment; // комментарии пользователей, к примеру, "подарочная упаковка"
    //я считаю коммент нужен к заказу, а не к корзине

    @Column()
    private LocalDateTime addedTime;

    //автоматическая инициализация
    @PrePersist
    protected void onCreate() {
        if (addedTime == null) {
            addedTime = LocalDateTime.now();
        }

        if (priceAddition == null && product != null) {
            priceAddition = product.getCurrentPrice();
        }
    }

    public BigDecimal getPriceAtAdd(BigDecimal currentPrice) {
        return priceAddition;
    }
}
