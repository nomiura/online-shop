package domain.service;

import domain.entity.Currency;
import domain.entity.TransactionType;
import domain.entity.Wallet;
import domain.entity.WalletTransaction;
import domain.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
@Getter
@Setter
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public Wallet createWallet(Long accountId, Currency currency) {
        Wallet wallet = walletRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new WalletNotFoundException(account.getId()));

        return null;
    }

    @Override
    public Wallet getWallet(UUID walletId) {


        return null;
    }

    @Override
    public WalletTransaction debit(UUID walletId, BigDecimal amount, TransactionType type, String idempotencyKey, Long orderId) {
        return null;
    }
}
