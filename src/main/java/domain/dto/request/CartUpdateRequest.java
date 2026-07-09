package domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartUpdateRequest {
    @NotNull(message = "Нужен ID товара, чтобы изменить количество")
    private Long productId;

    @NotNull(message = "Укажите новое количество")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    @Max(value = 999, message = "Количество не может быть больше 999")
    private Integer quantity;
}
