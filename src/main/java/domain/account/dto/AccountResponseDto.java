package domain.account.dto;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private Long id;
    private String email;
}
