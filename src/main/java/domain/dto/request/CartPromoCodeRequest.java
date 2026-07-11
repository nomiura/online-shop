package domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartPromoCodeRequest {
    @NotBlank(message = "Промокод не может быть пустым")
    private String promoCode;
}
