package domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


//ledger для аудита и проверки операций

@Setter
@Getter
@Entity
@Table(name = "wallet_transactions",
        indexes = {
            @Index(name = "idx_wallet_created", columnList = "wallet_id, created_at"),
            @Index(name = "idx_idempotency", columnList =  "idempotency_key", unique = true)
        })
public class WalletTransaction {
    @Id
    private UUID id;

    @Column(name="wallet_id", nullable = false)
    private UUID walletId;

    //ссылка на парную запись в double-entry (например, id кошелька магаза)
    @Column(name = "counter_wallet_id")
    private UUID counterWalletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // DEPOSIT, WITHDRAW, HOLD, CAPTURE, REFUND, PURCHASE

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount; //всегда положительный, знак дается transactionType выше

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter; //снимок баланрса после операции

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name ="order_id")
    private Long orderId; //если операция связана с заказом

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // PENDING, COMPLETED, FAILED, REVERSED

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
