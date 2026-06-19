package domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
//@RequiredArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Wallet wallet; //кошелек принадлежит акку

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Cart cart; //корзина принадлежит акку

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id") //внешний ключ в таблице address
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "account_addresses",
//            joinColumns = @JoinColumn(name = "account_id"))
    private List<Address> addresses;

    private String email; // уникальный
    private String phone; // уникальный
    private String password;

    private String city;

    private Long ordersCount = 0L;

    private LocalDateTime createdDate;




}
