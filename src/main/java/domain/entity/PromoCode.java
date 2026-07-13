package domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "discount_type")
    private DiscountType discountType; //PERCENT, FIXED

    @Column(name = "discount_value")
    private BigDecimal discountValue; //если в процентах, то хранить так 5% = 0.05

    @Column(name = "min_order_amount")
    private BigDecimal minOrderAmount;

    @Column(name = "max_discount")
    private BigDecimal maxDiscount;

    @Column(name = "used_count")
    private Integer usedCount = 0;

    @Column(name = "valid_from")
    private LocalDateTime timeValidFrom;

    @Column(name = "valid_to")
    private LocalDateTime timeValidTo;

    @Column(name = "get_usage_limit")
    private Integer usageLimit;

    @Column(name = "isActive")
    private Boolean isActive = true;

    @Override
    public String toString() {
        return code;
    }


    //можно доработать, но в падлу
//    // 4. Можно ли комбинировать с другими скидками
//    if (cart.getDiscountAmount() != null && cart.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
//        // Если есть другая скидка - проверяем, разрешено ли комбинировать
//        if (!promo.getCanCombine()) {
//            throw new PromoCodeException("Промокод нельзя комбинировать с другими скидками");
//        }
//    }
//
//    // 5. Только для новых пользователей
//    if (promo.getOnlyForNewUsers()) {
//        if (!userService.isNewUser(cart.getAccountId())) {
//            throw new PromoCodeException("Промокод только для новых пользователей");
//        }
//    }
//
//    // 6. Проверка на товары
//    if (promo.getRequiredProductIds() != null && !promo.getRequiredProductIds().isEmpty()) {
//        boolean hasRequiredProduct = cart.getItems().stream()
//                .anyMatch(item -> promo.getRequiredProductIds().contains(item.getProductId()));
//
//        if (!hasRequiredProduct) {
//            throw new PromoCodeException(
//                    "Для применения промокода добавьте в корзину: " +
//                            productService.getNames(promo.getRequiredProductIds())
//            );
//        }
//    }
}
