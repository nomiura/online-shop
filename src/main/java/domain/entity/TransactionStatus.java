package domain.entity;

public enum TransactionStatus {
    PENDING,        // ожидает выполнения
    COMPLETED,      // выполнено
    FAILED,         // сбой
    REVERSED        // отменено
}
