package domain.account.service;

import domain.account.dto.AccountRequestDto;
import domain.account.dto.AccountResponseDto;

public interface AccountService {
    AccountResponseDto createAccount(AccountRequestDto accountRequestDto);
    AccountResponseDto updateAccount(AccountRequestDto accountRequestDto);
    AccountResponseDto deleteAccount(AccountRequestDto accountRequestDto);
    AccountResponseDto getAccount(AccountRequestDto accountRequestDto);
}
