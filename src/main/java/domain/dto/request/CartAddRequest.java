package domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartAddRequest {
    @NotNull(message = "Нужен ID товара, чтобы добавить")
    private Long productId;

    @Min(value = 1, message = "Укажите количество больше 0")
    @Max(value = 999, message = "Укажите количество меньше 999")
    private Integer quantity = 1;
}
