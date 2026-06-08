package domain.account.mapper;

import domain.account.dto.AccountResponseDto;
import domain.account.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponseDto accountToAccountDto(Account account) {
        if (account == null) return null;

        AccountResponseDto accountDto = new AccountResponseDto();
        accountDto.setId(account.getId());
        accountDto.setEmail(account.getEmail());

        return  accountDto;
    }

    public Account accountDtoToAccount(AccountResponseDto accountDto) {
        if (accountDto == null) return null;

        Account account = new Account();
        account.setId(accountDto.getId());
        account.setEmail(accountDto.getEmail());
        account.setPassword();
    }
}
