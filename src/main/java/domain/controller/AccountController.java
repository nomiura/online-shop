package domain.controller;

import domain.dto.request.AccountCreateRequest;
import domain.dto.request.AccountPatchRequest;
import domain.dto.request.AccountUpdateRequest;
import domain.dto.response.AccountResponseDto;
import domain.mapper.AccountMapper;
import domain.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

//HTTP Request (браузер) → Controller → Service → Repository → Database
//И обратно:
//Database → Repository → Service → Controller → HTTP Response (браузер)

//Контроллер отвечает за:
//        └── HTTP-слой (вход/выход из приложения)
//        ├── получение HTTP-запроса
//        ├── валидация через @Valid
//        ├── преобразование
//        ├── вызов сервиса
//        └── формирование HTTP-ответа
//Controller - принимает http-запрос, валидирует входные данные(проверяет, что мыло корректное, к примеру, через @Valid),
// вызывает нужный метод сервиса, возвращает http-ответ(статус + данные)
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long id) { //подставляет айдишник

        log.info("Request to get user: userId={}", id);
        AccountResponseDto dto = accountService.getAccountById(id);
        return ResponseEntity.ok(dto);
    }

//    @GetMapping("/me")
//    public ResponseEntity<AccountResponseDto> getCurrentAccount() {
//        log.info("Request to get current account");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserEmail = authentication.getName();
//
//        // Ищем пользователя по email
//        AccountResponseDto response = accountService.getAccountByEmail(currentUserEmail);
//        return ResponseEntity.ok(response);
//    }


    @PostMapping("/register")
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid  @RequestBody AccountCreateRequest request) { //@RequestBody Jackson превращает из json в класс
        // ВСЕ проверки в DTO + сервисе, здесь НИЧЕГО нет
        //controller получает dto из json
        //передаем в сервис для бизнес-логики??
        //превращаем entity -> responseDto

        // 1. Передаём request в сервис, сервис возвращает DTO (не Entity!)
        AccountResponseDto response = accountService.createAccount(request);

        // 2. ID берём из response (там есть поле id)
        URI location = URI.create("/account/" + response.getId());

        // 3. Возвращаем 201 Created с location и body
        return ResponseEntity
                .created(location)
                .body(response);
    }

    //полное обновление
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> fullUpdateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountUpdateRequest request) {
        AccountResponseDto response = accountService.fullUpdateAccount(id,request);
        return ResponseEntity.ok(response);
    }

    //частичное обновление
    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountPatchRequest request) {

        AccountResponseDto response = accountService.updateAccount(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountById(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return  ResponseEntity.noContent().build();
    }
}
