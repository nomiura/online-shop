package domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@Check(constraints = "discount_percent BETWEEN 0 AND 100")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false)

    private Integer discountPercent = 0;

    private String image;

    @Column(name = "quantity_available")
    private Integer quantityAvailable;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "supplier")
    private String supplier;

    public BigDecimal getEffectivePrice() {
        if (discountPercent == null || discountPercent == 0) {
            return currentPrice;
        }
        return currentPrice
                .multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public boolean isInStock() {
        return quantityAvailable != null && quantityAvailable > 0;
    }

}
