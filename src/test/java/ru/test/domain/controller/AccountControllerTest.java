package ru.test.domain.controller;

import org.springframework.http.MediaType;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import domain.controller.AccountController;
import domain.dto.response.AccountResponseDto;
import domain.entity.Account;
import domain.mapper.AccountMapper;
import domain.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

//@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest { //public не нужен, он по дефолту генерит этот модификатор

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountMapper accountMapper;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .build();
    }


    //GET
    //-------------------------------------------------------------------------

    @Test
    void getAccountById_ShouldReturnAccount_WhenExists() throws Exception {
        Long id = 1L;
        AccountResponseDto accountResponseDto = new AccountResponseDto(id, "nyanya@mail.ru");

        when(accountService.getAccountById(id)).thenReturn(accountResponseDto);

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect((ResultMatcher) jsonPath("$.id").value(id))
                .andExpect((ResultMatcher) jsonPath("$.email").value("nyanya@mail.ru"));

        verify(accountService, times(1)).getAccountById(id);

    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenDoesNotExist() throws Exception {
        Long id = null;
        AccountResponseDto accountResponseDto = new AccountResponseDto(null, null);



    }


    //POST
    //-------------------------------------------------------------------------
    @Test
    void createAccount_ShouldCreateAccount() throws Exception {
        AccountResponseDto accountResponseDto = new AccountResponseDto(2L, "ivan@gmail.com");

        when(accountService.createAccount(any())).thenReturn(accountResponseDto);

        String json = """
        {
          "email":"ivan@gmail.com",
          "password":"12345678",
          "phone":"89144443331",
          "city":"Москва"
        }
        """;
        mockMvc.perform(post("account/register")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/account/2"))
                .andExpect(jsonPath("$.id").value(2));



    }

    @Test
    void createAccount_ShouldReturn400WhenEmailIsBlank() throws Exception {
        AccountResponseDto accountResponseDto = new AccountResponseDto(null, null);
    }

}
