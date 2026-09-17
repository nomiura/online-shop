package domain.service;

import domain.entity.Currency;
import domain.entity.TransactionType;
import domain.entity.Wallet;
import domain.entity.WalletTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    // --- Создание / чтение ---
    Wallet createWallet(Long accountId, Currency currency);
    Wallet getWallet(UUID walletId);
    //List<Wallet> getWalletsByAccountId(Long accountId); ---- это пока не нужно, потенциально кошельки для разных валют


    // --- Пополнение / вывод ---
    WalletTransaction debit(UUID walletId,
                            BigDecimal amount,
                            TransactionType type,
                            String idempotencyKey,
                            Long orderId);


    // --- Переводы между кошельками ---

    // --- Двухфазная оплата заказа ---


    // --- Прямое списание (без холда, редко) ---

    // --- Возвраты ---

    // --- Корректировки (только саппорт/админ, с аудитом) ---

    // --- Отмена операции (не удаление!) ---

    // --- Админ-операции ---
}
