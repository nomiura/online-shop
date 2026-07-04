package domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts") //добавить редис для кэширования
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //связь с пользователем
    @OneToOne
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

//    //для неавторизованных пользователей (UUID сессии) ----------> Алина, надо делать? чето пиздс лень, там куча всего придется сделать
//    @Column(name = "sessionId", length = 100)
//    private String sessionId;

    //товары в корзине
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> items = new ArrayList<>();


    //итоговые суммы
    @Column(name = "subtotal_price", precision = 12, scale = 2)
    private BigDecimal subtotalPrice = BigDecimal.ZERO; //стоимость товаров без скидки

    @Column(name = "total_price", precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO; //итоговая сумма всех товаров, включая скидку

    @Column(name = "discount_price", precision = 12, scale = 2) //все скидки вместе
    private BigDecimal discountPrice =  BigDecimal.ZERO;

    @Column(name = "total_quantity") //общее кол-во товаров в корзине
    private Integer totalQuantity = 0;


    @Column(name= "delivery_price")
    private BigDecimal deliveryPrice = BigDecimal.ZERO; //стоимость доставки


    //промокоды
    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "promo_code_discount",
            precision = 12, scale = 2)
    //precision точность(общее кол-во всех цифр в числе - до и после запятой), scale масштаб (кол-во цифр после запятой)
    private BigDecimal promoCodeDiscount = BigDecimal.ZERO; //сумма скидки по промокоду
}
