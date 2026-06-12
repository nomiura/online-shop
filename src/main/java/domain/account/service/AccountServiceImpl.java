package domain.account.service;


import domain.account.dto.AccountResponseDto;
import domain.account.entity.Account;
import domain.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


//проверки (существует ли уже мыло?), преобразование(хеширование пароля), координация между репозиториями(если нужно сохранить данные в 3 таблицы),
//транзакции

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponseDto createAccount(String email, String phone, String password, String city) {
        Account account = new Account();
        account.setEmail(email);
        account.setPhone(phone);
        account.setPassword(password);
        account.setCity(city);

        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        Account account =  accountRepository.findById(id)
                .orElseThrow(() ->  {
                    log.warn("Аккаунт с ID {} не найден в БД", id);
                    return new RuntimeException("Аккаунт не найден");
                });
        return account;
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найден."));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
