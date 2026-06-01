package domain.account.entity;


import domain.cart.entity.Cart;
import domain.payment.entity.Wallet;
import domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sun.jvm.hotspot.debugger.Address;
import domain.payment.entity.*;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Wallet wallet;

    private Cart cart;

    private List<Address> addresses;

    private String city;

    private Long ordersCount;

    private String password;




}
