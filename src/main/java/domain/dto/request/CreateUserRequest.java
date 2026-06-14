package domain.dto.request;

import domain.entity.Sex;
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
        private String username;
        private LocalDate birthday;
        private Sex sex;
        private List<String> addresses;
}