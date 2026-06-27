package domain.exception;

public class QuantityLimitExceededException extends RuntimeException {
    public QuantityLimitExceededException(String message) {
        super(message);
    }
}
