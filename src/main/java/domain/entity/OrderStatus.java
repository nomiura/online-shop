package domain.entity;

public enum OrderStatus {
    CREATED,
    APPROVED,
    PAID,
    REJECTED,
    CANCELLED,
    COMPLETED,
    DELEVERED;

    public boolean isTerminal() {
        return this == REJECTED || this == CANCELLED || this == DELEVERED;
    }

    public boolean isCreated() {
        return this == CREATED || this == APPROVED;
    }
}
