package domain.dto.request;

import domain.entity.Sex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

        @NotBlank(message = "Имя не может быть пустым")
        private String username;

        @NotNull(message = "Дата рождения не может быть пустой")
        @Past(message = "Дата рождения не может быть в будущем")
        private LocalDate birthday;

        @NotNull(message = "Пол не может быть пустым")
        private Sex sex;

        private List<String> addresses;
}