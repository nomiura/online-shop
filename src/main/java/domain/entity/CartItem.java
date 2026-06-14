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

    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;
    private Integer quantity;
    private String comment; // комментарии пользователей, к примеру, "подарочная упаковка"
    private BigDecimal priceAtAdd; // на момент добавления в корзину, чтобы не менялась после изменения цены товара
    private LocalDateTime addedTime;

}
