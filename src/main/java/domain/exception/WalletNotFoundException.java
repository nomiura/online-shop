package domain.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(Long accountId) {
        super("Кошелек не найден у аккаунта:" + accountId);
    }
}
