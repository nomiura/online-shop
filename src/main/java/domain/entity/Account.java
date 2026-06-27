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
    private List<Address> addresses;

    @Column(nullable = false, unique = true)
    private String email; // уникальный

    @Column(nullable = false, unique = true)
    private String phone; // уникальный

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String city;

    private Long ordersCount = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) default 'INDIVIDUAL'")
    private AccountType accountType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void onCreate() {
        createdDate = LocalDateTime.now();
    }
}
