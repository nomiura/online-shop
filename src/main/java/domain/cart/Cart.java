package domain.cart;


import domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}
