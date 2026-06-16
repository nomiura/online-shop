package domain.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AccountCreateRequest {

    @NotBlank(message = "Почта не должна быть пустой")
    @Email(message = "Неправильный формат почты")
    private String email;

    @NotBlank(message = "Необходим пароль")
    @Size(min = 6, max = 16, message = "Размер пароля должен быть от 6 до 16 символов включительно")
    private String password;

    @NotBlank(message = "Телефон обязателен")
    @Size(min = 11, max = 11, message = "Неправильный ввод")
    private String phone;

    @NotBlank(message = "Город обязателен")
    private String city;
}
