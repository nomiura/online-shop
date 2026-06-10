package domain.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


//реквест для пользователя
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateRequest { // тут нужны валидации
    @Email(message = "Почта не должна быть пустой")
    @NotBlank(message = "Почта должна быть корректной")
    private String email;

    @NotBlank(message = "Необходим пароль")
    @Size(min = 6, max = 16, message = "Размер пароля должен быть от 6 до 16 символов включительно")
    private String password;

    @Size(min = 11, max = 11)
    private String phone;
    private String city;
}
