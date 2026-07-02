package domain.service;

import domain.dto.request.AccountCreateRequest;
import domain.dto.request.AccountPatchRequest;
import domain.dto.request.AccountUpdateRequest;
import domain.dto.response.AccountResponseDto;

public interface AccountService {
    AccountResponseDto createAccount(AccountCreateRequest accountCreateRequest);
    AccountResponseDto updateAccount(Long id, AccountPatchRequest accountPatchRequest);
    AccountResponseDto fullUpdateAccount(Long id,AccountUpdateRequest accountUpdateRequest);
    void deleteAccountById(Long accountId);
    AccountResponseDto getAccountById(Long id);
}
