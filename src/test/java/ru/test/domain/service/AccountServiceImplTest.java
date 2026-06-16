package ru.test.domain.service;

import domain.dto.request.AccountCreateRequest;
import domain.dto.response.AccountResponseDto;
import domain.entity.Account;
import domain.entity.User;
import domain.entity.Wallet;
import domain.mapper.AccountMapper;
import domain.repository.AccountRepository;
import domain.service.AccountService;
import domain.service.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    //Mock`создает подделку,@InjectMocks создает реальный объект
    //https://www.youtube.com/watch?v=EODRGCrZu5A

    @InjectMocks
    private AccountServiceImpl accountServiceImpl; //то, что вызываем

    @Mock
    private AccountRepository accountRepository; //имитируем поведение = мокаем (заглушка)

    @Mock
    private AccountMapper accountMapper;


    @Test
    void createAccount() {
        Account account = new Account("");
    }

    @Test
    void getAccountById() {
        //заготовки
        Long id = 1L;
        Account account = new Account(id, new User(), new Wallet(),"nyanya@mail.ru", "parol123", "8983134848", "Москва");
        AccountResponseDto accountResponseDto = new AccountResponseDto(id,"nyanya@mail.ru");

        //AccountCreateRequest request = new AccountCreateRequest("nyanya@mail.ru", "parol123", "8983134848", "Москва");


        //как только будет обращения к мокам, отдаем то, что создали сверху
        //то есть задали поведение
        when(accountRepository.findById(id)).thenReturn((Optional.of(account));
        when(accountMapper.accountToResponseDto(account)).thenReturn(accountResponseDto());

        //вызываем у сервиса метод гетАкк
        //конкретно проверяем действие
        Optional<AccountResponseDto> result = accountServiceImpl.getAccountById(id);


        assertTrue(result.isPresent());
        assertEquals(accountResponseDto, result.get());

        verify(accountMapper, times(1)).accountToResponseDto(account);  //проверяем, корректно ли количество раз вызвалось
    }
}
