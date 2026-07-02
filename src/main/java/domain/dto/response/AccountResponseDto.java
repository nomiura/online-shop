package domain.dto.response;

import lombok.*;

//отдаем пользователю
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private Long id;
    private String email;
}
