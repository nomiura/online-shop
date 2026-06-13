package domain.dto.response;

import domain.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private LocalDate birthday;
    private Sex sex;
    private String address;
}
