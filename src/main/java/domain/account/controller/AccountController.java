package domain.account.controller;

import domain.account.dto.AccountRequestDto;
import domain.account.dto.AccountResponseDto;
import domain.account.entity.Account;
import domain.account.mapper.AccountMapper;
import domain.account.service.*;
import domain.account.service.AccountServiceImpl;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

//HTTP Request (браузер) → Controller → Service → Repository → Database
//И обратно:
//Database → Repository → Service → Controller → HTTP Response (браузер)

//Контроллер отвечает за:
//        └── HTTP-слой (вход/выход из приложения)
//        ├── получение HTTP-запроса
//        ├── валидация
//        ├── преобразование
//        ├── вызов сервиса
//        └── формирование HTTP-ответа
//Controller - принимает http-запрос, валидирует входные данные(проверяет, что мыло корректное, к примеру, через @Valid),
// вызывает нужный метод сервиса, возвращает http-ответ(статус + данные)
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public AccountController(AccountServiceImpl accountService, AccountMapper accountMapper) {   //autowired не нужен, тк 1 конструктор
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid  @RequestBody AccountRequestDto accountRequestDto) {
        // ВСЕ проверки в DTO + сервисе, здесь НИЧЕГО нет
        //controller получает dto из json
        //передаем в сервис для бизнес-логики
        Account createdAccount = accountService.createAccount(accountRequestDto);
        //превращаем entity -> responseDto
        AccountResponseDto response = accountMapper.accountToAccountDto(createdAccount);

        URI location = URI.create("/accounts/" + createdAccount.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }


    @PutMapping("/update")
    public ResponseEntity<AccountResponseDto> updateAccount(
            AccountRequestDto accountRequestDto) {




    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountResponseDto> getAccount(
            @PathVariable Long id, //подставляет айдишник
            //@AuthenticationPrincipal or @PreAuthorize
            Account currentAccount) {

        return ResponseEntity.ok(accountService.getAccount(id));
    }




    @DeleteMapping("/delete")


}
