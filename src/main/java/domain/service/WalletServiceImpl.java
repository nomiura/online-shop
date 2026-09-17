package domain.service;

import domain.entity.Currency;
import domain.entity.TransactionType;
import domain.entity.Wallet;
import domain.entity.WalletTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {
    @Override
    public Wallet createWallet(Long accountId, Currency currency) {
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
