package domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Cart cart;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id") //внешний ключ в таблице address
    private List<Address> addresses;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

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

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Order> orders = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdDate = LocalDateTime.now();
    }
}
