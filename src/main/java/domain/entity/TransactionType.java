package domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum TransactionType {
    DEPOSIT,     // пополнение извне (эквайринг, перевод с карты)
    WITHDRAWAL,  // вывод наружу (на карту, в банк)
    TRANSFER_IN, // входящий перевод с другого кошелька
    TRANSFER_OUT,// исходящий перевод на другой кошелёк
    PURCHASE,    // списание за заказ
    REFUND,      // возврат по отменённому заказу
    FEE,         // комиссия
    ADJUSTMENT,  // ручная корректировка (админ/саппорт) — с аудитом
    REVERSAL     // отмена предыдущей операции (никогда не DELETE!)
}