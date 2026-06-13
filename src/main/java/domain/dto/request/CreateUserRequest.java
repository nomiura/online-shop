package domain.dto.request;

import domain.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
        private String username;
        private String phone;
        private String email;
        private String password;
        private LocalDate birthday;
        private Sex sex;
        private String address;
}