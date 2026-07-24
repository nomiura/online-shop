package domain.controller;

import domain.dto.request.AccountCreateRequest;
import domain.dto.request.AccountPatchRequest;
import domain.dto.request.AccountUpdateRequest;
import domain.dto.response.AccountResponseDto;
import domain.entity.AccountType;
import domain.exception.AccountNotFoundException;
import domain.mapper.AccountMapper;
import domain.service.AccountServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest { //public не нужен, он по дефолту генерит этот модификатор

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // для сериализации в json

    @MockitoBean
    private AccountServiceImpl accountService;

    @MockitoBean
    private AccountMapper accountMapper;

    //GET
    //-------------------------------------------------------------------------

    @Test
    @DisplayName("Should return 200 OK and dto the existing account")
    void getAccountById_shouldReturnAccount() throws Exception {
        Long id = 1L;
        AccountResponseDto accountResponseDto = new AccountResponseDto(id, "nyanya@mail.ru");

        when(accountService.getAccountById(id)).thenReturn(accountResponseDto);

        String dtoJson = objectMapper.writeValueAsString(accountResponseDto);

        mockMvc.perform(get("/account/{id}", id))
                .andExpect(status().isOk()) //200 OK
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("nyanya@mail.ru"));

        verify(accountService, times(1)).getAccountById(id);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when account do not exist")
    void getAccountById_shouldReturnNotFound_whenAccountDoesNotExist() throws Exception {
        Long id = 999L;

        when(accountService.getAccountById(id)).thenThrow(new AccountNotFoundException("Account not found with id: " + id));

        mockMvc.perform(get("/account/{id}", id))
                .andExpect(status().isNotFound()) //404 NOT FOUND
                .andExpect(jsonPath("$.message").value("Account not found with id: 999"));

        verify(accountService, times(1)).getAccountById(id);
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when id is invalid")
    void getAccountById_ShouldReturnBadRequest_WhenIdIsInvalid() throws MethodArgumentNotValidException, Exception {
        Long id = -1L;

        mockMvc.perform(get("/account/{id}", id))
                .andExpect(status().isBadRequest()); //400 BAD REQUEST
    }


    //POST
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("Should create account 201 CREATED")
    void createAccount_shouldCreateAccount() throws Exception {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
                "ivan@gmail.com",
                "123456",
                "89134567890",
                "Санкт-Петербург",
                AccountType.INDIVIDUAL);
        AccountResponseDto accountResponseDto = new AccountResponseDto(2L, "ivan@gmail.com");
        when(accountService.createAccount(accountCreateRequest)).thenReturn(accountResponseDto);

        String requestJson = objectMapper.writeValueAsString(accountCreateRequest);

        mockMvc.perform(post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("ivan@gmail.com"));

        verify(accountService, times(1)).createAccount(any(AccountCreateRequest.class));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when email is blank in create request")
    void createAccount_shouldReturnBadRequest_whenEmailIsBlank() throws Exception {
        AccountResponseDto accountResponseDto = new AccountResponseDto(null, null);

        AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
                "",
                "123456",
                "89134567890",
                "Санкт-Петербург",
                AccountType.INDIVIDUAL
        );

        String requestJson = objectMapper.writeValueAsString(accountCreateRequest);

        mockMvc.perform(post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest()); // 400

        verify(accountService, never()).createAccount(any(AccountCreateRequest.class));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when email is invalid in create request")
    void createAccount_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
                "invalid_email",
                "123456",
                "89134567890",
                "Санкт-Петербург",
                AccountType.INDIVIDUAL
        );

        String requestJson = objectMapper.writeValueAsString(accountCreateRequest);

        mockMvc.perform(post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest()); //400

        verify(accountService, never()).createAccount(any(AccountCreateRequest.class));
    }

    @Test
    void createAccount_shouldReturnBadRequest_whenPasswordIsBlank() throws Exception {
        AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
                "invalid_email",
                "",
                "89134567890",
                "Санкт-Петербург",
                AccountType.INDIVIDUAL
        );

        String requestJson = objectMapper.writeValueAsString(accountCreateRequest);

        mockMvc.perform(post("/account/register")
                        .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isBadRequest()); //404

        verify(accountService, never()).createAccount(any(AccountCreateRequest.class));
    }


    //PUT
    //-------------------------------------------------------------------------

    @Test
    void fullUpdateAccount_shouldUpdateAccount() throws Exception {
        Long id = 3L;
        AccountUpdateRequest request = new AccountUpdateRequest(
                "e-girl@pedoffki.com",
                "netakusya23",
                "89831399881",
                "Нижний Новгород"
        );
        AccountResponseDto responseDto = new AccountResponseDto(id, "e-girl@pedoffki.com");

        when(accountService.fullUpdateAccount(id, request)).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/account/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("e-girl@pedoffki.com"));

        verify(accountService, times(1)).fullUpdateAccount(id, request);
    }

    @Test
    void fullUpdateAccount_shouldReturnNotFound_whenAccountDoesNotExist() throws Exception {
        Long id = 999L;

        AccountUpdateRequest request = new AccountUpdateRequest(
                "nyanya@mail.ru",
                "123456",
                "89134567890",
                "Санкт-Петербург"
        );

        when(accountService.fullUpdateAccount(id, request)).thenThrow(new AccountNotFoundException("Account not found with id: " + id));

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/account/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found with id: 999"));

        verify(accountService, times(1)).fullUpdateAccount(id, request);
    }


    //PATCH
    //-------------------------------------------------------------------------

    @Test
    void updateAccount_shouldUpdateAccount() throws Exception {
        Long id = 3L;
        AccountPatchRequest patchRequest = new AccountPatchRequest(
                "neirokit@yandex.ru",
                "123456",
                null,
                null
        );
        AccountResponseDto responseDto = new AccountResponseDto(id, "neirokit@yandex.ru");

        when(accountService.updateAccount(id, patchRequest)).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(patchRequest);

        mockMvc.perform(patch("/account/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("neirokit@yandex.ru"));

        verify(accountService, times(1)).updateAccount(id, patchRequest);
    }

//1. Клиент отправляет PATCH запрос
//2. Spring проверяет @Valid в контроллере
//3. Валидация находит пустой email ""
//            4. ❌ Валидация проваливается -> выбрасывается MethodArgumentNotValidException
//5. ❌ Контроллер НЕ вызывает сервис
//6. ✅ GlobalExceptionHandler ловит исключение и возвращает 400

    @Test
    void updateAccount_shouldReturnNotFound_whenAccountDoesNotExist() throws Exception { //404
        Long id = 999L;

        AccountPatchRequest request = new AccountPatchRequest(
                "nyanya@mail.ru",
                null,
                "89134567890",
                null
        );

        when(accountService.updateAccount(id, request)).thenThrow(new AccountNotFoundException("Account not found with id: " + id));
        String jsonRequest = objectMapper.writeValueAsString(request);
        mockMvc.perform(patch("/account/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found with id: 999"));


        verify(accountService, times(1)).updateAccount(id, request);
    }

    @Test
    void updateAccount_shouldReturnBadRequest_whenEmailIsBlank() throws Exception { //400
        Long id = 999L;

        AccountPatchRequest patchRequest = new AccountPatchRequest(
                "",
                "123456",
                null,
                null
        );

        String jsonPatchRequest = objectMapper.writeValueAsString(patchRequest);

        mockMvc.perform(patch("/account/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPatchRequest))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).updateAccount(id, patchRequest);
    }


    //DELETE
    //-------------------------------------------------------------------------
    @Test
    void deleteAccount_shouldDeleteAccount() throws Exception {
        Long id = 3L;
        doNothing().when(accountService).deleteAccountById(id); //для void используем doNothing() или doThrow()

        mockMvc.perform(delete("/account/{id}", id))
                .andExpect(status().isNoContent()); //204 NO CONTENT - стандарт для DELETE

        verify(accountService, times(1)).deleteAccountById(id);
    }

    @Test
    void deleteAccount_shouldReturnNotFound_whenAccountDoesNotExist() throws Exception {
        Long id = 999L;
        doThrow(new AccountNotFoundException("Account not found with id: " + id)).when(accountService).deleteAccountById(id);

        mockMvc.perform(delete("/account/{id}", id))
                .andExpect(status().isNotFound()); //404

        verify(accountService, times(1)).deleteAccountById(id);
    }
}
