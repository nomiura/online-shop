package domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart; // ← вот оно//удалить после теста

    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;
    private Integer quantity;
    private String comment; // комментарии пользователей, к примеру, "подарочная упаковка"
    //я считаю коммент нужен к заказу, а не к корзине
    private BigDecimal priceAtAdd; // на момент добавления в корзину, чтобы не менялась после изменения цены товара
    private LocalDateTime addedTime;

}
