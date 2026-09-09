package domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    private BigDecimal balance;

    private List<Order> orders;

    private BigDecimal cashback;

    private Currency currency;

    private Timestamp timestamp; //время создания и последнего изменения кошелька
}
