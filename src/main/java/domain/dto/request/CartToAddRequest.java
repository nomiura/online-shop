package domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartToAddRequest {
    @NotNull(message = "Нужен ID товара, чтобы добавить")
    private Long productId;

    @Min(value = 1, message = "Укажите количество")
    private Integer quantity = 1;
}
