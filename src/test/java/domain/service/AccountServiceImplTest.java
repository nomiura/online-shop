package domain.service;

import domain.OnlineShopApplication;
import domain.dto.request.AccountCreateRequest;
import domain.dto.request.AccountPatchRequest;
import domain.dto.request.AccountUpdateRequest;
import domain.dto.response.AccountResponseDto;
import domain.entity.Account;
import domain.entity.AccountType;
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

    //подготовь -> выполни -> проверь
    //arrange -> act -> assert
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
        AccountResponseDto expectedDto = new AccountResponseDto(id, "nyanya@mail.ru");

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
    void getAccountById_shouldThrowException_whenAccountNotFound() {
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
                "Санкт-Петербург",
                AccountType.INDIVIDUAL
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
    void createAccount_shouldThrowException_whenEmailAlreadyExists() {
        AccountCreateRequest request = new AccountCreateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург",
                AccountType.INDIVIDUAL
        );

        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> accountServiceImpl.createAccount(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists: " + request.getEmail());

        verify(accountRepository, times(1)).existsByEmail(request.getEmail());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw DataAccessException when repository fails")
    void createAccount_shouldThrowException_whenRepositoryFails() {
        AccountCreateRequest request = new AccountCreateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург",
                AccountType.INDIVIDUAL
        );

        Account account = new Account();
        account.setEmail("ivan@gmail.com");


        when(accountMapper.accountToEntity(request)).thenReturn(account);
        when(accountRepository.save(account)).thenThrow(new DataAccessException("Database error"));

        assertThatThrownBy(() -> accountServiceImpl.createAccount(request))
                .isInstanceOf(DataAccessException.class)
                .hasMessage("Database error");

        verify(accountMapper, times(1)).accountToEntity(request);
        verify(accountRepository, times(1)).save(account);
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

        AccountResponseDto expectedResponse = new AccountResponseDto(id, "ivan@gmail.com");

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
    void fullUpdateAccount_shouldThrowException_whenEmailAlreadyExists() {
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
    @Test
    @DisplayName("Should return AccountResponseDto when account is updated")
    void updateAccount_shouldReturnDto_whenAccountIsUpdated() {
        Long id = 1L;
        Account oldAccount = new Account();
        oldAccount.setId(id);
        oldAccount.setEmail("travyanoe1488@gmail.com");
        oldAccount.setPassword("12345673");
        oldAccount.setPhone("89134567890");
        oldAccount.setCity("Донецк");
        oldAccount.setAccountType(AccountType.INDIVIDUAL);

        AccountPatchRequest request = new AccountPatchRequest(
                "travyanoe@gmail.com",
                "12345673",
                "89134567890",
                "Санкт-Петербург"
        );

        String encodedPassword = "encoded123456";
        Account updatedAccount = new Account();
        updatedAccount.setId(id);
        updatedAccount.setEmail("travyanoe@gmail.com");
        updatedAccount.setPassword(encodedPassword);
        updatedAccount.setPhone(null);
        updatedAccount.setCity("Санкт-Петербург");

        AccountResponseDto expectedDto = new AccountResponseDto(id, "travyanoe@gmail.com");
        // стаббинг!
        when(accountRepository.findById(id)).thenReturn(Optional.of(oldAccount));
        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
        when(accountRepository.save(oldAccount)).thenReturn(updatedAccount);
        when(accountMapper.accountToResponseDto(updatedAccount)).thenReturn(expectedDto);

        AccountResponseDto actualResponse = accountServiceImpl.updateAccount(id, request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(id);
        assertThat(actualResponse.equals(expectedDto));

        verify(accountRepository, times(1)).findById(id);
        verify(accountRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, times(1)).encode(request.getPassword());
        verify(accountRepository, times(1)).save(oldAccount);
        verify(accountMapper, times(1)).accountToResponseDto(updatedAccount);
    }

    @Test
    @DisplayName("Should return exception when email exists")
    void updateAccount_shouldReturnEmailAlreadyExistsException_whenEmailExists() {
        Long accountIdToUpdate = 2L;
        String newEmail = "travyanoe@gmail.com";
        Account accountToUpdate = new Account();
        accountToUpdate.setId(accountIdToUpdate);
        accountToUpdate.setEmail("travyanoe1488@gmail.com");
        accountToUpdate.setPassword("12345673");
        accountToUpdate.setPhone("89134567890");
        accountToUpdate.setCity("Донецк");
        accountToUpdate.setAccountType(AccountType.INDIVIDUAL);

        AccountPatchRequest request = new AccountPatchRequest(
                newEmail,
                "12345673",
                "89134567890",
                "Санкт-Петербург"
        );

        // стаббинг!
        when(accountRepository.findById(accountIdToUpdate)).thenReturn(Optional.of(accountToUpdate));
        when(accountRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> accountServiceImpl.updateAccount(accountIdToUpdate, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists: " + newEmail);

        verify(accountRepository, times(1)).findById(accountIdToUpdate);
        verify(accountRepository, times(1)).existsByEmail(request.getEmail());
        verify(passwordEncoder, never()).encode(request.getPassword());
        verify(accountRepository, never()).save(accountToUpdate);
        verify(accountMapper, never()).accountToResponseDto(accountToUpdate);
    }


    //DELETE
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("Should delete account by id")
    void deleteAccountById_shouldDeleteAccountById() {
        Long id = 1L;
        Account accountToDelete = new Account();
        accountToDelete.setId(id);
        accountToDelete.setEmail("test@mail.ru");

        when(accountRepository.existsById(id)).thenReturn(true);
        doNothing().when(accountRepository).deleteById(id); // просто проверяем, что он вызвался

        //when
        accountServiceImpl.deleteAccountById(id);

        //then
        verify(accountRepository, times(1)).existsById(id);
        verify(accountRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should return exception when account with id do not exist")
    void deleteAccountById_shouldAccountNotFoundException_whenAccountIdDoNotExist() {
        Long id = 88L;
        when(accountRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> accountServiceImpl.deleteAccountById(id))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found by id: " + id);

        verify(accountRepository, times(1)).existsById(id);
        verify(accountRepository, never()).deleteById(id);
    }
}
