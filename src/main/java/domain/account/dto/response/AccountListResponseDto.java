package domain.account.dto.response;

import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountListResponseDto {
    private List<AccountResponseDto> accounts;
    private Integer totalCount;
}