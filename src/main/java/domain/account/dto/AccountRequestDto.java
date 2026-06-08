package domain.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto { // тут нужны валидации
    @Email(message = "Почта не должна быть пустой")
    @NotBlank(message = "Почта должна быть корректной")
    private String email;

    @Size(min = 6, max = 20)
    private String password;

    @Size(min = 11, max = 11)
    private String phone;
    private String city;
}
