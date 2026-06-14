
package domain.service;


import domain.dto.request.AccountCreateRequest;
import domain.dto.request.AccountPatchRequest;
import domain.dto.request.AccountUpdateRequest;
import domain.dto.response.AccountResponseDto;
import domain.entity.Account;
import domain.exception.AccountNotFoundException;
import domain.exception.EmailAlreadyExistsException;
import domain.mapper.AccountMapper;
import domain.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//проверки (существует ли уже мыло?), преобразование(хеширование пароля), координация между репозиториями(если нужно сохранить данные в 3 таблицы),
//транзакции

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public AccountResponseDto createAccount(AccountCreateRequest accountCreateRequest) {
        log.info("Creating account...");

        Account account = accountMapper.accountToEntity(accountCreateRequest);
        accountRepository.save(account);

        log.info("Account is created");
        return accountMapper.accountToResponseDto(account);
    }

    @Transactional
    @Override
    public AccountResponseDto updateAccount(Long id, AccountPatchRequest request) {
        log.info("Updating account with id: {}", id + "...");

        String email = request.getEmail();

        Account updatedAccount = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account not found by id: " + id));

        //проверка на уникальность мыла
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: "  + request.getEmail());
        }

        if (request.getEmail() != null) {
            updatedAccount.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            updatedAccount.setPassword(passwordEncoder.encode(request.getPassword())); //шифрование пароля
        }

        if (request.getCity() != null) {
            updatedAccount.setCity(request.getCity());
        }

        if (request.getPhone() != null) {
            updatedAccount.setPhone(request.getPhone());
        }

        accountRepository.save(updatedAccount);

        log.info("Account is updated with id: {}", id);
        return accountMapper.accountToResponseDto(updatedAccount);
    }
    @Transactional
    @Override
    public AccountResponseDto fullUpdateAccount(Long id, AccountUpdateRequest request) {
        log.info("Fully updating account with id: {}", id + "...");

        Account updatedAccount = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account not found by id: " + id));

        //проверка на уникальность мыла
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: "  + request.getEmail());
        }

        updatedAccount.setEmail(request.getEmail());
        updatedAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        updatedAccount.setPhone(request.getPhone());
        updatedAccount.setCity(request.getCity());

        accountRepository.save(updatedAccount);

        return accountMapper.accountToResponseDto(updatedAccount);
    }

    @Transactional
    @Override
    public void deleteAccountById(Long id) {
        log.info("Deleting account with id: {}", id + "...");

        accountRepository.deleteById(id);

        log.info("Account is deleted with id: {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountResponseDto getAccountById(Long id) {
        log.info("Getting account with id: {}", id + "...");

        Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account not found by id: " + id));

        log.info("Account is found with id: {}", id);
        return accountMapper.accountToResponseDto(account);
    }
}
