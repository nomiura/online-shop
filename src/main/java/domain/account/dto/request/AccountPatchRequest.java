package domain.account.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class AccountPatchRequest {
    @Email(message = "Неправильный формат почты")  // НЕТ @NotBlank!
    private String email;  // опционально (если null - не трогаем)

    @Size(min = 6, max = 16, message = "Размер пароля должен быть от 6 до 16 символов")
    private String password;  // опционально (если null - не трогаем)

    @Size(min = 11, max = 11, message = "Телефон должен быть 11 цифр")
    private String phone;  // опционально (если null - не трогаем)

    private String city;  // опционально (если null - не трогаем)
}
