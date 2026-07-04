package domain.entity;

public enum OrderStatus {
    CREATED,
    APPROVED,
    PAID,
    REJECTED,
    CANCELLED,
    COMPLETED,
    DELIVERED;

    public boolean isTerminal() {
        return this == REJECTED || this == CANCELLED || this == DELIVERED;
    }

    public boolean isCreated() {
        return this == CREATED || this == APPROVED;
    }
}
