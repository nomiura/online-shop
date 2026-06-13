package domain.dto.request;

import domain.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserRequest {
    private Long id;

    private String phone;
    private LocalDate birthday;
    private Sex sex;
    private String password;
    private  String email;
    private String address;
    private String username;
}
