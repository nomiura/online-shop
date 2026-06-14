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
public class UpdateUserRequest {


    private String username;


    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;


    private Sex sex;

    private List<String> addresses;

}
