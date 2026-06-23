package domain.service;

import domain.OnlineShopApplication;
import domain.dto.request.AccountCreateRequest;
import domain.dto.request.AccountUpdateRequest;
import domain.dto.response.AccountResponseDto;
import domain.entity.Account;
import domain.exception.AccountNotFoundException;
import domain.exception.DataAccessException;
import domain.exception.EmailAlreadyExistsException;
import domain.mapper.AccountMapper;
import domain.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = OnlineShopApplication.class)
class AccountServiceImplTest {
    //Mock`создает подделку,@InjectMocks создает реальный объект
    //https://www.youtube.com/watch?v=EODRGCrZu5A

    @InjectMocks
    private AccountServiceImpl accountServiceImpl; //то, что вызываем

    @Mock
    private AccountRepository accountRepository; //имитируем поведение = мокаем (заглушка)

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private PasswordEncoder passwordEncoder;


    //GET
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("Should return AccountResponseDto when account exists")
    void getAccountById() {
        //заготовки
        Long id = 1L;
        Account account = new Account();
        account.setId(id);
        account.setEmail("nyanya@mail.ru");

        AccountResponseDto expectedDto = new AccountResponseDto(id,"nyanya@mail.ru");

        //как только будет обращения к мокам, отдаем то, что создали сверху
        //то есть задали поведение
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountMapper.accountToResponseDto(account)).thenReturn(expectedDto);

        //вызываем у сервиса метод гетАкк
        //конкретно проверяем действие
        AccountResponseDto actualDto = accountServiceImpl.getAccountById(id);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);

        verify(accountRepository, times(1)).findById(id);
        verify(accountMapper, times(1)).accountToResponseDto(account);  //проверяем, корректно ли количество раз вызвалось
    }

    @Test
    @DisplayName("Should throw AccountNotFoundException when account doesn't exist")
    void getAccountById_ShouldThrowException_WhenAccountNotFound() {
        Long id = 999L;

        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountServiceImpl.getAccountById(id));
    }


    //POST
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("Should return AccountResponseDto when account is created")
    void createAccount() {
        AccountCreateRequest request = new AccountCreateRequest(
                "nyanya@gmail.ru",
                "123456",
                "89134567890",
                "Санкт-Петербург"
        );

        Account account = new Account();
        account.setEmail("nyanya@gmail.ru");
        account.setPassword("123456");
        account.setPhone("89134567890");
        account.setCity("Санкт-Петербург");

        Account savedAccount = new Account();
        savedAccount.setId(1L);
        savedAccount.setEmail("ivan@gmail.com");
        savedAccount.setPassword("123456");
        savedAccount.setPhone("89134567890");
        savedAccount.setCity("Санкт-Петербург");

        AccountResponseDto expectedResponse = new AccountResponseDto(1L, "nyanya@gmail.ru");

        when(accountMapper.accountToEntity(request)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(savedAccount);
        when(accountMapper.accountToResponseDto(savedAccount)).thenReturn(expectedResponse);

        AccountResponseDto actualResponse = accountServiceImpl.createAccount(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo(expectedResponse);
        assertThat(actualResponse.getId()).isEqualTo(1L);
        assertThat(actualResponse.getEmail()).isEqualTo("nyanya@gmail.ru");

        verify(accountMapper, times(1)).accountToEntity(request);
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).accountToResponseDto(savedAccount);
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void createAccount_ShouldThrowException_WhenEmailAlreadyExists() {
        AccountCreateRequest request = new AccountCreateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург"
        );

        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> accountServiceImpl.createAccount(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists: " + request.getEmail());

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw DataAccessException when repository fails")
    void createAccount_ShouldThrowException_WhenRepositoryFails() {
        AccountCreateRequest request = new AccountCreateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург"
        );

        Account account = new Account();
        account.setEmail("ivan@gmail.com");


        when(accountMapper.accountToEntity(request)).thenReturn(account);
        when(accountRepository.save(account)).thenThrow(new DataAccessException("Database error"));

        assertThatThrownBy(() -> accountServiceImpl.createAccount(request))
                .isInstanceOf(DataAccessException.class)
                .hasMessage("Database error");

        verify(accountMapper,times(1)).accountToEntity(request);
        verify(accountRepository,times(1)).save(account);
        verify(accountMapper, never()).accountToResponseDto(any(Account.class));
    }



    //PUT
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("Should return AccountResponseDto when account is full updated")
    void fullUpdateAccount() {
        //given
        Long id = 1L;
        String encodedPassword = "encoded123456";
        AccountUpdateRequest request = new AccountUpdateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург"
        );

        Account account = new Account();
        account.setId(id);
        account.setEmail("old@gmail.com");
        account.setPassword("old123");
        account.setPhone("89001234567");
        account.setCity("Москва");

        Account updatedAccount = new Account();
        updatedAccount.setId(id);
        updatedAccount.setEmail("ivan@gmail.com");
        updatedAccount.setPassword(encodedPassword);
        updatedAccount.setPhone("89134567890");
        updatedAccount.setCity("Санкт-Петербург");

        AccountResponseDto expectedResponse = new AccountResponseDto(id,   "ivan@gmail.com");

        //when
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        when(accountRepository.save(account)).thenReturn(updatedAccount);
        when(accountMapper.accountToResponseDto(updatedAccount)).thenReturn(expectedResponse);

        AccountResponseDto actualResponse = accountServiceImpl.fullUpdateAccount(id, request);

        //then
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(id);
        assertThat(actualResponse.getEmail()).isEqualTo("ivan@gmail.com");

        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(accountRepository, times(1)).save(account);
        verify(accountMapper, times(1)).accountToResponseDto(updatedAccount);
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void fullUpdateAccount_ShouldThrowException_WhenEmailAlreadyExists() {
        Long id = 1L;
        AccountUpdateRequest request = new AccountUpdateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург"
        );

        Account existingAccount = new Account();
        existingAccount.setId(id);
        existingAccount.setEmail("old@gmail.com");

        when(accountRepository.findById(id)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

        //when -> then
        assertThatThrownBy(() -> accountServiceImpl.fullUpdateAccount(id, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists: " + request.getEmail());

        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(accountRepository, never()).save(any());
        verify(accountMapper, never()).accountToResponseDto(any());
    }


    //PATCH
    //-------------------------------------------------------------------------



    //DELETE
    //-------------------------------------------------------------------------
}
