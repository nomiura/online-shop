package domain.mapper;

import domain.dto.request.AccountCreateRequest;
import domain.dto.response.AccountResponseDto;
import domain.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {



    public AccountResponseDto accountToResponseDto(Account account) {
        if (account == null) return null;

        AccountResponseDto accountDto = new AccountResponseDto();
        accountDto.setId(account.getId());
        accountDto.setEmail(account.getEmail());

        return  accountDto;
    }


    //регистрация dto -> entity
    public Account accountToEntity(AccountCreateRequest request) {
        if (request == null) return null;

        Account account = new Account();
        //account.setId(request.getId());
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());

        account.setPhone(request.getPhone());
        account.setCity(request.getCity());

        return account;
    }
}
