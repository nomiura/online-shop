package domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // GenerationType.UUID — это стратегия генерации уникальных
    // идентификаторов в JPA (Java Persistence API), которая автоматически создает
    // 128-битные значения (16 байтн) типа UUID для первичных ключей сущностей
    //Распределенные системы: Позволяет создавать уникальные ID на стороне приложения до сохранения в базу, исключая
    // конфликты ключей при слиянии данных из разных источников.Безопасность: Случайные идентификаторы сложнее перебрать
    // или угадать по порядку (в отличие от автоинкрементируемых IDENTITY чисел 1, 2, 3...)
    private UUID id;

    // @OneToOne(cascade = CascadeType.PERSIST)
    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    //текущ баланс, денормализация для быстрого чтения
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO; //RUB

    //заблокированные ср-ва (для pending-заказов)
    @Column(name = "held_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal heldBalance = BigDecimal.ZERO;

    @Version        //optimistic lock
    private Long version;

    private Currency currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
