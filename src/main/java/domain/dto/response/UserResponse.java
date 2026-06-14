package domain.dto.response;

import domain.entity.Sex;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private LocalDate birthday;

    private Sex sex;
    private List<String> addresses;
}
