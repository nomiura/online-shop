package domain.account.service;

import domain.account.dto.request.AccountCreateRequest;
import domain.account.dto.request.AccountPatchRequest;
import domain.account.dto.request.AccountUpdateRequest;
import domain.account.dto.response.AccountResponseDto;
import domain.account.entity.Account;

public interface AccountService {
    AccountResponseDto createAccount(AccountCreateRequest accountCreateRequest);

    AccountResponseDto updateAccount(Long id, AccountPatchRequest accountPatchRequest);
    AccountResponseDto fullUpdateAccount(Long id,AccountUpdateRequest accountUpdateRequest);

    void deleteAccountById(Long accountId);


    AccountResponseDto getAccountById(Long id);
}
